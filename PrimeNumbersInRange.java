public class PrimeNumbersInRange {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int n1 = 1;
        int n2 = 6000;

        System.out.println("Prime numbers between " + n1 + " and " + n2 + " are:");
        for (int i = n1; i <= n2; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }

    // Function to check if a number is prime
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
