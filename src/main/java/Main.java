public class Main {

    public static void main(String[] args) {
        Solution solution = new Solution();

        solution.deleteAll();
        solution.populate();

        solution.databaseStatistics();

        solution.runAllTests();
    }

}
