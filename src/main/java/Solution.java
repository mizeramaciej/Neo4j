import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Solution {

    private final GraphDatabase graphDatabase = GraphDatabase.createDatabase();

    public void databaseStatistics() {
        System.out.println(graphDatabase.runCypher("CALL db.labels()"));
        System.out.println(graphDatabase.runCypher("CALL db.relationshipTypes()"));
        System.out.println(graphDatabase.runCypher("CALL db.schema()"));
    }

    public void addCar(String car, Map<String, String> map) {
        graphDatabase.runCypher(buildCypher(car, "Car", map));
    }

    public void addPerson(String person, Map<String, String> map) {
        graphDatabase.runCypher(buildCypher(person, "Person", map));
    }

    public void addLikeRelation(String firstName, String lastName, String brand, String model) {
        graphDatabase.runCypher(String.format("MATCH (p:Person {firstName: '%s',lastName: '%s'})," +
                "(c:Car {brand: '%s',model: '%s'}) " +
                "CREATE (p)-[:LIKES]->(c)", firstName, lastName, brand, model));
    }

    public void addOwnsRelation(String firstName, String lastName, String brand, String model) {
        graphDatabase.runCypher(String.format("MATCH (p:Person {firstName: '%s',lastName: '%s'})," +
                "(c:Car {brand: '%s',model: '%s'}) " +
                "CREATE (p)-[:OWNS]->(c)", firstName, lastName, brand, model));
    }

    public void addDesignedByRelation(String firstName, String lastName, String brand, String model) {
        graphDatabase.runCypher(String.format("MATCH (p:Person {firstName: '%s',lastName: '%s'})," +
                "(c:Car {brand: '%s',model: '%s'}) " +
                "CREATE (p)<-[:DESIGNED_BY]-(c)", firstName, lastName, brand, model));
    }

    private String buildCypher(String nodeName, String type, Map<String, String> propertyMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE (" + nodeName + ":" + type + " {");
        Iterator<Map.Entry<String, String>> iter = propertyMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(": '");
            sb.append(entry.getValue());
            sb.append("'");
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("})");
        return String.valueOf(sb);
    }

    public String getAllNodes() {
        return graphDatabase.runCypher(String.format("MATCH (allNodes) RETURN allNodes"));
    }

    private String findCarByBrand(final String brand) {
        return graphDatabase.runCypher(String.format("MATCH (cars:Car {brand: '%s'}) RETURN cars", brand));
    }

    public String findLikedCarsByPerson(final String firstName, final String lastName) {
        return graphDatabase.runCypher(String.format(
                "MATCH (person:Person)-[:LIKES]->(LikedCars:Car) WHERE person.firstName = '%s' AND person.lastName = '%s' RETURN person,LikedCars", firstName, lastName));
    }

    public String findOwnedCarsByPerson(final String firstName, final String lastName) {
        return graphDatabase.runCypher(String.format(
                "MATCH (person:Person)-[:OWNS]->(OwnedCars:Car) WHERE person.firstName = '%s' AND person.lastName = '%s' RETURN person,OwnedCars", firstName, lastName));
    }

    public String findDesignerOfCar(final String brand, final String model) {
        return graphDatabase.runCypher(String.format(
                "MATCH (designer:Person)<-[:DESIGNED_BY]-(car:Car) WHERE car.brand = '%s' AND car.model = '%s' RETURN designer,car", brand, model));
    }

    public String findRelationOfPerson(final String firstName, final String lastName) {
        return graphDatabase.runCypher(String.format("MATCH (person:Person)-[relations]->(node) WHERE " +
                "person.firstName = '%s' AND person.lastName = '%s' RETURN person,relations,node", firstName, lastName));
    }

    public String findRelationOfCar(final String brand, final String model) {
        return graphDatabase.runCypher(String.format("MATCH (car:Car)<-[relations]-(node) WHERE " +
                "car.brand = '%s' AND car.model = '%s' RETURN car,relations,node", brand, model));
    }

    public String findPaths(final String first1, final String last1, final String first2, final String last2) {
        // Path is max 5 in lenght
        return graphDatabase.runCypher(String.format("MATCH paths=(p1:Person {firstName:'%s',lastName:'%s'})" +
                "-[:LIKES|:OWNS|:DESIGNED_BY*..5]-(p2:Person {firstName:'%s',lastName:'%s'}) RETURN paths LIMIT 25", first1, last1, first2, last2));
    }

    public String findShortestPath(final String first1, final String last1, final String first2, final String last2) {
        //Path is max 7 in lenght
        return graphDatabase.runCypher(String.format("MATCH shortestpath= shortestPath((p1:Person {firstName:'%s',lastName:'%s'})" +
                "-[:LIKES|:OWNS|:DESIGNED_BY*..7]-(p2:Person {firstName:'%s',lastName:'%s'})) RETURN shortestpath", first1, last1, first2, last2));
    }


    public void runAllTests() {
        System.out.println(getAllNodes());
        System.out.println(findLikedCarsByPerson("Maciek", "Mizera"));
        System.out.println(findOwnedCarsByPerson("Maciek", "Mizera"));
        System.out.println(findDesignerOfCar("Ford", "Focus"));
        System.out.println(findRelationOfPerson("Maciek", "Mizera"));
        System.out.println(findRelationOfCar("Ford", "Focus"));
        System.out.println(findPaths("Maciek", "Mizera", "Ola", "Olak"));
        System.out.println(findShortestPath("Maciek", "Mizera", "Ola", "Olak"));
        System.out.println(findPaths("Maciek", "Mizera", "Josh", "Kappa"));
        System.out.println(findShortestPath("Maciek", "Mizera", "Josh", "Kappa"));
        System.out.println(findPaths("Maciek", "Mizera", "Jan", "Jank"));
        System.out.println(findShortestPath("Maciek", "Mizera", "Jan", "Jank"));
    }

    public void populate() {
        Map<String, String> map = new HashMap<>();
        map.put("brand", "Ford");
        map.put("model", "Focus");
        addCar("FordFocus", map);
        map.clear();

        map.put("brand", "Ford");
        map.put("model", "Mondeo");
        addCar("FordMondeo", map);
        map.clear();

        map.put("brand", "Ford");
        map.put("model", "Edge");
        addCar("FordEdge", map);
        map.clear();

        map.put("brand", "Ford");
        map.put("model", "Light");
        addCar("FordLight", map);
        map.clear();

        map.put("firstName", "Maciek");
        map.put("lastName", "Mizera");
        addPerson("MMizera", map);
        map.clear();

        map.put("firstName", "Jan");
        map.put("lastName", "Jank");
        addPerson("JJank", map);
        map.clear();

        map.put("firstName", "Ola");
        map.put("lastName", "Olak");
        addPerson("OOlak", map);
        map.clear();

        map.put("firstName", "Josh");
        map.put("lastName", "Kappa");
        addPerson("JKappa", map);
        map.clear();

        map.put("firstName", "Mark");
        map.put("lastName", "Kappa");
        addPerson("MKappa", map);
        map.clear();

        addLikeRelation("Maciek", "Mizera", "Ford", "Focus");
        addLikeRelation("Maciek", "Mizera", "Ford", "Mustang");
        addLikeRelation("Ola", "Olak", "Ford", "Focus");
        addLikeRelation("Ola", "Olak", "Ford", "Mustang");
        addLikeRelation("Jan", "Jank", "Ford", "Edge");
        addLikeRelation("Jan", "Jank", "Ford", "Light");
        addLikeRelation("Jan", "Jank", "Ford", "Mustang");
        addLikeRelation("Jan", "Jank", "Ford", "Focus");
        addLikeRelation("Ola", "Olak", "Ford", "Light");
        addLikeRelation("Ola", "Olak", "Ford", "Edge");

        addOwnsRelation("Ola", "Olak", "Ford", "Mustang");
        addOwnsRelation("Maciek", "Mizera", "Ford", "Mustang");
        addOwnsRelation("Maciek", "Mizera", "Ford", "Focus");
        addOwnsRelation("Josh", "Kappa", "Ford", "Focus");
        addOwnsRelation("Josh", "Kappa", "Ford", "Mustang");
        addOwnsRelation("Josh", "Kappa", "Ford", "Edge");
        addOwnsRelation("Mark", "Kappa", "Ford", "Edge");
        addOwnsRelation("Mark", "Kappa", "Ford", "Light");
        addOwnsRelation("Mark", "Kappa", "Ford", "Focus");
        addOwnsRelation("Mark", "Kappa", "Ford", "Mustang");


        addDesignedByRelation("Josh", "Kappa", "Ford", "Light");
        addDesignedByRelation("Josh", "Kappa", "Ford", "Edge");
        addDesignedByRelation("Josh", "Kappa", "Ford", "Focus");
        addDesignedByRelation("Josh", "Kappa", "Ford", "Mustang");
        addDesignedByRelation("Mark", "Kappa", "Ford", "Light");
        addDesignedByRelation("Mark", "Kappa", "Ford", "Edge");
        addDesignedByRelation("Mark", "Kappa", "Ford", "Focus");
        addDesignedByRelation("Mark", "Kappa", "Ford", "Mustang");
    }

    public void deleteAll() {
        graphDatabase.runCypher("MATCH (n) DETACH DELETE n");
    }
}