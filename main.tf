terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
    null = {
      source  = "hashicorp/null"
      version = "3.2.3"
    }
  }
}

variable "deploy-machine" {
  type = string
  sensitive = true
}

variable "deploy-machine-ssh-key-path" {
  type = string
  sensitive = true
}

provider "docker" {
  # host = "ssh://root@${var.deploy-machine}"
}

resource "docker_network" "kafka_network" {
  name = "kafka-network"
}

variable "kafka_ui_username" {
  type    = string
  sensitive = true
}

variable "kafka_ui_password" {
  type    = string
  sensitive = true
}

# resource "null_resource" "copy-kafka-files" {
#   # triggers = {
#   #   init_checksum = join("", [
#   #     for f in sort(fileset("${path.cwd}/kafka/kafka-ui", "**")) :
#   #     filesha256("${path.cwd}/kafka/kafka-ui${f}")
#   #   ])
#   # }
#
#   provisioner "remote-exec" {
#     inline = [
#       "mkdir -p /tmp/kafka/kafka-ui/"
#     ]
#   }
#
#   provisioner "file" {
#     source      = "${path.cwd}/kafka/kafka-ui/"
#     destination = "/tmp/kafka/kafka-ui/"
#   }
#
#   connection {
#     type        = "ssh"
#     host        = var.deploy-machine
#     user        = "root"
#     private_key = file(var.deploy-machine-ssh-key-path)
#     agent = true
#   }
#
# }
#
#
# resource "null_resource" "copy-grafana-files-and-folders" {
#   # triggers = {
#   #   init_checksum = join("", [
#   #     for f in sort(fileset("${path.cwd}/grafana", "**")) :
#   #     filesha256("${path.cwd}/grafana/${f}")
#   #   ])
#   # }
#
#   provisioner "remote-exec" {
#     inline = [
#       "mkdir -p /tmp/grafana/"
#     ]
#   }
#
#   provisioner "file" {
#     source      = "${path.cwd}/grafana/"
#     destination = "/tmp/grafana/"
#   }
#
#   connection {
#     type        = "ssh"
#     host        = var.deploy-machine
#     user        = "root"
#     private_key = file(var.deploy-machine-ssh-key-path)
#     agent = true
#   }
#
# }
#
# resource "null_resource" "copy-mysql-init-queries" {
#   # triggers = {
#   #   init_checksum = join("", [
#   #     for f in sort(fileset("${path.cwd}/mysql/init", "**")) :
#   #     filesha256("${path.cwd}/mysql/init/${f}")
#   #   ])
#   # }
#
#   provisioner "remote-exec" {
#     inline = [
#       "mkdir -p /tmp/mysql/init/"
#     ]
#   }
#
#   provisioner "file" {
#     source      = "${path.cwd}/mysql/init/"
#     destination = "/tmp/mysql/init/"
#   }
#
#   connection {
#     type        = "ssh"
#     host        = var.deploy-machine
#     user        = "root"
#     private_key = file(var.deploy-machine-ssh-key-path)
#     agent = true
#   }
# }

resource "docker_container" "kafka" {
  name  = "kafka"
  image = "confluentinc/cp-kafka:7.2.1"

  ports {
    internal = 9092
    external = 9092
  }
  ports {
    internal = 9997
    external = 9997
  }

  env = [
    "KAFKA_BROKER_ID=1",
    "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT",
    "KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:29092,PLAINTEXT_HOST://kafka:9092",
    "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1",
    "KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0",
    "KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1",
    "KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1",
    "KAFKA_JMX_PORT=9997",
    "KAFKA_JMX_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=kafka -Dcom.sun.management.jmxremote.rmi.port=9997",
    "KAFKA_PROCESS_ROLES=broker,controller",
    "KAFKA_NODE_ID=1",
    "KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka:29093",
    "KAFKA_LISTENERS=PLAINTEXT://kafka:29092,CONTROLLER://kafka:29093,PLAINTEXT_HOST://kafka:9092",
    "KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT",
    "KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER",
    "KAFKA_LOG_DIRS=/tmp/kraft-combined-logs"
  ]

  mounts {
    source    = "${path.cwd}/kafka/kafka-ui/scripts/update_run.sh"
    target    = "/tmp/update_run.sh"
    type      = "bind"
    read_only = true
  }

  command = [
    "bash", "-c",
    "if [ ! -f /tmp/update_run.sh ]; then echo \"ERROR: missing update_run.sh\" && exit 1; else /tmp/update_run.sh && /etc/confluent/docker/run; fi"
  ]

  depends_on = [
    # null_resource.copy-kafka-files
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
    aliases = ["kafka"]
  }
}

resource "docker_container" "schema_registry" {
  name  = "schema-registry"
  image = "confluentinc/cp-schema-registry:7.2.1"

  ports {
    internal = 8085
    external = 8085
  }

  env = [
    "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=PLAINTEXT://kafka:29092",
    "SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL=PLAINTEXT",
    "SCHEMA_REGISTRY_HOST_NAME=schema-registry",
    "SCHEMA_REGISTRY_LISTENERS=http://schema-registry:8085",
    "SCHEMA_REGISTRY_SCHEMA_REGISTRY_INTER_INSTANCE_PROTOCOL=http",
    "SCHEMA_REGISTRY_LOG4J_ROOT_LOGLEVEL=INFO",
    "SCHEMA_REGISTRY_KAFKASTORE_TOPIC=_schemas"
  ]

  depends_on = [
    docker_container.kafka,
    # null_resource.copy-kafka-files
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }
}

resource "docker_container" "kafka_connect" {
  name  = "kafka-connect"
  image = "confluentinc/cp-kafka-connect:7.2.1"

  ports {
    internal = 8083
    external = 8083
  }

  env = [
    "CONNECT_BOOTSTRAP_SERVERS=kafka:29092",
    "CONNECT_GROUP_ID=compose-connect-group",
    "CONNECT_CONFIG_STORAGE_TOPIC=_connect_configs",
    "CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR=1",
    "CONNECT_OFFSET_STORAGE_TOPIC=_connect_offset",
    "CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR=1",
    "CONNECT_STATUS_STORAGE_TOPIC=_connect_status",
    "CONNECT_STATUS_STORAGE_REPLICATION_FACTOR=1",
    "CONNECT_KEY_CONVERTER=org.apache.kafka.connect.storage.StringConverter",
    "CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL=http://schema-registry:8085",
    "CONNECT_VALUE_CONVERTER=org.apache.kafka.connect.storage.StringConverter",
    "CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL=http://schema-registry:8085",
    "CONNECT_INTERNAL_KEY_CONVERTER=org.apache.kafka.connect.json.JsonConverter",
    "CONNECT_INTERNAL_VALUE_CONVERTER=org.apache.kafka.connect.json.JsonConverter",
    "CONNECT_REST_ADVERTISED_HOST_NAME=kafka-connect",
    "CONNECT_PLUGIN_PATH=/usr/share/java,/usr/share/confluent-hub-components"
  ]

  depends_on = [
    docker_container.kafka,
    docker_container.schema_registry
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }
}

resource "docker_container" "kafka_ui" {
  name  = "kafka-ui"
  image = "provectuslabs/kafka-ui:latest"

  ports {
    internal = 8080
    external = 8080
  }

  env = [
    "KAFKA_CLUSTERS_0_NAME=local",
    "KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:29092",
    "KAFKA_CLUSTERS_0_METRICS_PORT=9997",
    "KAFKA_CLUSTERS_0_SCHEMAREGISTRY=http://schema-registry:8085",
    "KAFKA_CLUSTERS_0_KAFKACONNECT_0_NAME=connect",
    "KAFKA_CLUSTERS_0_KAFKACONNECT_0_ADDRESS=http://kafka-connect:8083",
    "DYNAMIC_CONFIG_ENABLED=true",
    "AUTH_TYPE=LOGIN_FORM",
    "SPRING_SECURITY_USER_NAME=${var.kafka_ui_username}",
    "SPRING_SECURITY_USER_PASSWORD=${var.kafka_ui_password}"
  ]

  depends_on = [
    docker_container.kafka,
    docker_container.schema_registry,
    docker_container.kafka_connect
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }
}

resource "docker_container" "kafka_init_topics" {
  name  = "kafka-init-topics"
  image = "confluentinc/cp-kafka:7.2.1"

  command = [
    "bash", "-c",
    "echo Waiting for Kafka... && cub kafka-ready -b kafka:29092 1 30 && kafka-topics --create --topic stocks --partitions 3 --replication-factor 1 --if-not-exists --bootstrap-server kafka:29092"
  ]

  mounts {
    source    = "${path.cwd}/kafka/kafka-ui/data/message.json"
    target    = "/data/message.json"
    type      = "bind"
    read_only = true
  }

  depends_on = [
    docker_container.kafka,
    # null_resource.copy-kafka-files
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }
}

resource "null_resource" "stock-simulator-jar" {
  provisioner "local-exec" {
    command = "mvn clean package -f simulator/pom.xml -DskipTests"
  }
}

resource "docker_image" "stock-simulator" {
  name = "stock-simulator:latest"

  build {
    context    = "${path.cwd}/simulator"
    dockerfile = "Dockerfile"
  }

  depends_on = [
    null_resource.stock-simulator-jar
  ]
}

resource "docker_container" "stock-simulator" {
  name  = "stock-simulator"
  image = docker_image.stock-simulator.name

  ports {
    internal = 8081
    external = 8081
  }

  env = [
    "KAFKA_STOCKS_BOOSTRAP_SERVERS=${docker_container.kafka.name}:9092",
    "KAFKA_STOCKS_DEFAULT_TOPIC=stocks"
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }

  depends_on = [
    docker_container.kafka,
    docker_container.kafka_ui
  ]

}


resource "docker_container" "jobmanager" {
  name  = "flink-jobmanager"
  image = "apache/flink:1.18.1-scala_2.12-java17"
  hostname = "jobmanager"

  ports {
    internal = 8081
    external = 5555
  }

  env = [
    "FLINK_PROPERTIES=rest.address: 0.0.0.0\nrest.port: 8081\njobmanager.rpc.address: jobmanager"
  ]

  command = ["jobmanager"]

  networks_advanced {
    name = docker_network.kafka_network.name
    aliases = ["jobmanager"]
  }
}

resource "docker_container" "taskmanager" {
  name  = "flink-taskmanager"
  image = "apache/flink:1.18.1-scala_2.12-java17"

  depends_on = [
    docker_container.jobmanager
  ]

  env = [
    "FLINK_PROPERTIES=jobmanager.rpc.address: jobmanager\ntaskmanager.numberOfTaskSlots: 2"
  ]

  command = ["taskmanager"]

  networks_advanced {
    name = docker_network.kafka_network.name
    aliases = ["taskmanager"]
  }

}

resource "null_resource" "stock-processor-jar" {
  provisioner "local-exec" {
    command = "mvn clean package -f stock-processor/pom.xml -DskipTests"
  }
}

resource "docker_image" "stock-processor" {
  name = "stock-processor:latest"

  build {
    context    = "${path.module}/stock-processor"
    dockerfile = "Dockerfile"
  }

  depends_on = [
    null_resource.stock-processor-jar
  ]
}

variable "influx-db-sink-host" {
  type        = string
  sensitive   = true
  description = "Host to InfluxDB"
}

variable "influx-db-sink-token" {
  type        = string
  sensitive   = true
  description = "Token to connect to the InfluxDB instance"
}

variable "influx-db-sink-analysis-bucket" {
  type        = string
  sensitive   = true
  description = "Name of the Analysis bucket in InfluxDB"
}

variable "influx-db-sink-prediction-bucket" {
  type        = string
  sensitive   = true
  description = "Name of the Prediction bucket in InfluxDB"
}

resource "docker_container" "stock-processor" {
  image = docker_image.stock-processor.name
  name  = "stock-processor"

  env = [
    "KAFKA_BOOTSTRAP_SERVER=${docker_container.kafka.name}:29092",
    "INFLUX_DB_SINK_HOST=${var.influx-db-sink-host}",
    "INFLUX_DB_SINK_TOKEN=${var.influx-db-sink-token}",
    "INFLUX_DB_STOCK_ANALYSIS_BUCKET_NAME=${var.influx-db-sink-analysis-bucket}",
    "INFLUX_DB_STOCK_PREDICTIONS_BUCKET_NAME=${var.influx-db-sink-prediction-bucket}",
    "MYSQL_STOCK_DB_USERNAME=${var.mysql-user}",
    "MYSQL_STOCK_DB_PASSWORD=${var.mysql-password}",
    "MYSQL_STOCK_DB_URL=jdbc:mysql://${docker_container.mysql-db.name}:3306/${var.mysql-database}",
  ]

  depends_on = [
    docker_container.kafka,
    docker_container.kafka_ui,
    docker_container.stock-simulator,
    docker_container.jobmanager,
    docker_container.taskmanager,
    docker_container.mysql-db
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
  }

}

variable "grafana-password" {
  type = string
  sensitive = true
}

resource "docker_container" "grafana" {
  name = "grafana"
  image = "grafana/grafana:latest"

  ports {
    internal = 3000
    external = 3000
  }

  env = [
    "GF_SECURITY_ADMIN_PASSWORD=${var.grafana-password}"
  ]

  mounts {
    source = "${path.cwd}/grafana/data"
    target = "/var/lib/grafana"
    type   = "bind"
  }

  mounts {
    source = "${path.cwd}/grafana/logs"
    target = "/var/log/grafana"
    type   = "bind"
  }

  mounts {
    source = "${path.cwd}/grafana/plugins"
    target = "/var/lib/grafana/plugins"
    type   = "bind"
  }

  mounts {
    source = "${path.cwd}/grafana/provisioning"
    target = "/etc/grafana/provisioning"
    type   = "bind"
  }

  mounts {
    source = "${path.cwd}/grafana/dashboards"
    target = "/var/lib/grafana/dashboards"
    type   = "bind"
  }

  mounts {
    source = "${path.cwd}/grafana/grafana.ini"
    target = "/etc/grafana/grafana.ini:ro"
    type   = "bind"
  }

  depends_on = [
    docker_container.mysql-db,
    docker_container.stock-processor,
    # null_resource.copy-grafana-files-and-folders
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
    aliases = ["grafana"]
  }
}

variable "mysql-root-password" {
  type = string
  sensitive = true
}

variable "mysql-user" {
  type = string
  sensitive = true
}

variable "mysql-password" {
  type = string
  sensitive = true
}

variable "mysql-database" {
  type = string
  default = "stocks"
}

resource "docker_container" "mysql-db" {
  image = "mysql:8.0.33"
  name  = "mysql"

  ports {
    internal = 3306
    external = 4069
  }

  env = [
    "MYSQL_ROOT_PASSWORD=${var.mysql-root-password}",
    "MYSQL_USER=${var.mysql-user}",
    "MYSQL_PASSWORD=${var.mysql-password}",
    "MYSQL_DATABASE=${var.mysql-database}"
  ]

  mounts {
    source = "${path.cwd}/mysql/init"
    target = "/docker-entrypoint-initdb.d"
    type   = "bind"
    read_only = true
  }

  depends_on = [
    # null_resource.copy-mysql-init-queries
  ]

  networks_advanced {
    name = docker_network.kafka_network.name
    aliases = ["mysql"]
  }

}