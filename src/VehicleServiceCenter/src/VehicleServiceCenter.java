import java.sql.*;
import java.util.Scanner;

public class VehicleServiceCenter {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== VEHICLE SERVICE CENTER =====");
            System.out.println("1. Register Customer");
            System.out.println("2. Register Vehicle");
            System.out.println("3. Book Service");
            System.out.println("4. View Service History");
            System.out.println("5. Search Vehicle");
            System.out.println("6. View Customers");
            System.out.println("7. Exit");

            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try (Connection con = DBConnection.getConnection()) {

                switch (choice) {

                    case 1:
                        registerCustomer(con);
                        break;

                    case 2:
                        registerVehicle(con);
                        break;

                    case 3:
                        bookService(con);
                        break;

                    case 4:
                        serviceHistory(con);
                        break;

                    case 5:
                        searchVehicle(con);
                        break;

                    case 6:
                        viewCustomers(con);
                        break;

                    case 7:
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void registerCustomer(Connection con) throws Exception {

        System.out.print("Customer Name: ");
        String name = sc.nextLine();

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        String sql =
                "INSERT INTO customers(name,phone,email) VALUES(?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);

        ps.executeUpdate();

        System.out.println("Customer registered.");
    }

    static void registerVehicle(Connection con) throws Exception {

        System.out.print("Customer ID: ");
        int customerId = sc.nextInt();
        sc.nextLine();

        System.out.print("Vehicle Number: ");
        String number = sc.nextLine();

        System.out.print("Vehicle Model: ");
        String model = sc.nextLine();

        System.out.print("Vehicle Type: ");
        String type = sc.nextLine();

        String sql = """
                INSERT INTO vehicles
                (customer_id,vehicle_number,vehicle_model,vehicle_type)
                VALUES(?,?,?,?)
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, customerId);
        ps.setString(2, number);
        ps.setString(3, model);
        ps.setString(4, type);

        ps.executeUpdate();

        System.out.println("Vehicle registered.");
    }

    static void bookService(Connection con) throws Exception {

        System.out.print("Vehicle ID: ");
        int vehicleId = sc.nextInt();
        sc.nextLine();

        System.out.print("Service Type: ");
        String type = sc.nextLine();

        System.out.print("Amount: ");
        double amount = sc.nextDouble();

        String sql = """
                INSERT INTO service_records
                (vehicle_id,service_date,service_type,amount,status)
                VALUES(?,CURDATE(),?,?,?)
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, vehicleId);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.setString(4, "BOOKED");

        ps.executeUpdate();

        System.out.println("Service booked.");
    }

    static void serviceHistory(Connection con) throws Exception {

        System.out.print("Vehicle Number: ");
        String number = sc.nextLine();

        String sql = """
                SELECT c.name,
                       v.vehicle_number,
                       v.vehicle_model,
                       s.service_date,
                       s.service_type,
                       s.amount,
                       s.status
                FROM customers c
                JOIN vehicles v
                    ON c.customer_id = v.customer_id
                JOIN service_records s
                    ON v.vehicle_id = s.vehicle_id
                WHERE v.vehicle_number = ?
                ORDER BY s.service_date DESC
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, number);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            System.out.println("\nCustomer : "
                    + rs.getString("name"));

            System.out.println("Vehicle : "
                    + rs.getString("vehicle_model"));

            System.out.println("Date : "
                    + rs.getDate("service_date"));

            System.out.println("Service : "
                    + rs.getString("service_type"));

            System.out.println("Amount : "
                    + rs.getDouble("amount"));

            System.out.println("Status : "
                    + rs.getString("status"));
        }
    }

    static void searchVehicle(Connection con) throws Exception {

        System.out.print("Vehicle Number: ");
        String number = sc.nextLine();

        String sql = """
                SELECT c.name,
                       c.phone,
                       v.vehicle_number,
                       v.vehicle_model,
                       v.vehicle_type
                FROM customers c
                JOIN vehicles v
                    ON c.customer_id = v.customer_id
                WHERE v.vehicle_number = ?
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, number);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            System.out.println("Customer : "
                    + rs.getString("name"));

            System.out.println("Phone : "
                    + rs.getString("phone"));

            System.out.println("Vehicle : "
                    + rs.getString("vehicle_number"));

            System.out.println("Model : "
                    + rs.getString("vehicle_model"));

            System.out.println("Type : "
                    + rs.getString("vehicle_type"));

        } else {

            System.out.println("Vehicle not found.");
        }
    }

    static void viewCustomers(Connection con) throws Exception {

        String sql = "SELECT * FROM customers";

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            System.out.println(
                    rs.getInt("customer_id") + " | " +
                            rs.getString("name") + " | " +
                            rs.getString("phone") + " | " +
                            rs.getString("email")
            );
        }
    }
}