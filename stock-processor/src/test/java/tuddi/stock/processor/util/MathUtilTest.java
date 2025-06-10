package tuddi.stock.processor.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathUtilTest {

    private final Random random = new Random(42);

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 2000, 5000})
    void givenAListOfDoubleNumbersBetween0And1_havingTheSumAndSumOfSquares_shouldComputeTheStdSameAsTheNormalFormula(int n) {
        double[] nums = IntStream.range(0, n)
                .mapToDouble(_i -> random.nextDouble())
                .toArray();
        double expected = MathUtil.computeStandardDeviation(nums);

        double sum = 0.0;
        double sumOfSquares = 0.0;
        for (double num : nums) {
            sum += num;
            sumOfSquares += num * num;
        }

        double actual = MathUtil.computeStandardDeviation(sum, sumOfSquares, nums.length);

        assertEquals(expected, actual, 1e-8);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 2000, 5000})
    void givenAListOfDoubleNumbers_havingTheSumAndSumOfSquares_shouldComputeTheStdSameAsTheNormalFormula(int n) {
        double[] nums = IntStream.range(0, n)
                .mapToDouble(_i -> random.nextDouble() * _i)
                .toArray();
        double expected = MathUtil.computeStandardDeviation(nums);

        double sum = 0.0;
        double sumOfSquares = 0.0;
        for (double num : nums) {
            sum += num;
            sumOfSquares += num * num;
        }

        double actual = MathUtil.computeStandardDeviation(sum, sumOfSquares, nums.length);

        assertEquals(expected, actual, 1e-8);
    }


}