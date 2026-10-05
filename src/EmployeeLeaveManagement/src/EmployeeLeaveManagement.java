import java.sql.*;
import java.util.Scanner;

public class EmployeeLeaveManagement {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== EMPLOYEE LEAVE MANAGEMENT =====");
            System.out.println("1. Register Employee");
            System.out.println("2. Apply Leave");
            System.out.println("3. Approve Leave");
            System.out.println("4. Reject Leave");
            System.out.println("5. Check Leave Balance");
            System.out.println("6. View Leave Requests");
            System.out.println("7. Exit");

            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try (Connection con = DBConnection.getConnection()) {

                switch (choice) {

                    case 1:
                        registerEmployee(con);
                        break;

                    case 2:
                        applyLeave(con);
                        break;

                    case 3:
                        updateLeave(con, true);
                        break;

                    case 4:
                        updateLeave(con, false);
                        break;

                    case 5:
                        checkBalance(con);
                        break;

                    case 6:
                        viewRequests(con);
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

    static void registerEmployee(Connection con) throws Exception {

        System.out.print("Employee Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Department: ");
        String dept = sc.nextLine();

        con.setAutoCommit(false);

        try {

            String sql =
                    "INSERT INTO employees(name,email,department) VALUES(?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, dept);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            keys.next();

            int employeeId = keys.getInt(1);

            String balanceSql =
                    "INSERT INTO leave_balance(employee_id) VALUES(?)";

            PreparedStatement ps2 =
                    con.prepareStatement(balanceSql);

            ps2.setInt(1, employeeId);

            ps2.executeUpdate();

            con.commit();

            System.out.println("Employee registered.");
        }
        catch (Exception e) {

            con.rollback();
            System.out.println("Transaction rolled back.");

            throw e;
        }
    }

    static void applyLeave(Connection con) throws Exception {

        System.out.print("Employee ID: ");
        int employeeId = sc.nextInt();

        System.out.print("Leave days: ");
        int days = sc.nextInt();
        sc.nextLine();

        System.out.print("Leave type: ");
        String type = sc.nextLine();

        System.out.print("Reason: ");
        String reason = sc.nextLine();

        String sql = """
                INSERT INTO leave_requests
                (employee_id,leave_type,days,reason)
                VALUES(?,?,?,?)
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, employeeId);
        ps.setString(2, type);
        ps.setInt(3, days);
        ps.setString(4, reason);

        ps.executeUpdate();

        System.out.println("Leave applied successfully.");
    }

    static void updateLeave(Connection con, boolean approve)
            throws Exception {

        System.out.print("Request ID: ");
        int requestId = sc.nextInt();

        con.setAutoCommit(false);

        try {

            String findSql =
                    "SELECT employee_id,days,status " +
                            "FROM leave_requests WHERE request_id=?";

            PreparedStatement ps =
                    con.prepareStatement(findSql);

            ps.setInt(1, requestId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Request not found.");
                con.rollback();
                return;
            }

            int employeeId = rs.getInt("employee_id");
            int days = rs.getInt("days");
            String oldStatus = rs.getString("status");

            if (!oldStatus.equals("PENDING")) {
                System.out.println("Already processed.");
                con.rollback();
                return;
            }

            if (approve) {

                String balanceSql =
                        "UPDATE leave_balance " +
                                "SET used_leave = used_leave + ? " +
                                "WHERE employee_id=? " +
                                "AND total_leave - used_leave >= ?";

                PreparedStatement ps2 =
                        con.prepareStatement(balanceSql);

                ps2.setInt(1, days);
                ps2.setInt(2, employeeId);
                ps2.setInt(3, days);

                int rows = ps2.executeUpdate();

                if (rows == 0) {
                    System.out.println("Insufficient leave balance.");
                    con.rollback();
                    return;
                }
            }

            String updateSql =
                    "UPDATE leave_requests SET status=? " +
                            "WHERE request_id=?";

            PreparedStatement ps3 =
                    con.prepareStatement(updateSql);

            ps3.setString(1, approve ? "APPROVED" : "REJECTED");
            ps3.setInt(2, requestId);

            ps3.executeUpdate();

            con.commit();

            System.out.println(
                    approve ? "Leave approved." : "Leave rejected."
            );

        } catch (Exception e) {

            con.rollback();
            throw e;
        }
    }

    static void checkBalance(Connection con) throws Exception {

        System.out.print("Employee ID: ");
        int id = sc.nextInt();

        String sql =
                "SELECT total_leave,used_leave " +
                        "FROM leave_balance WHERE employee_id=?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            int total = rs.getInt("total_leave");
            int used = rs.getInt("used_leave");

            System.out.println("Total Leave : " + total);
            System.out.println("Used Leave  : " + used);
            System.out.println("Remaining   : " + (total - used));
        }
    }

    static void viewRequests(Connection con) throws Exception {

        String sql = """
                SELECT l.request_id,
                       e.name,
                       l.leave_type,
                       l.days,
                       l.status
                FROM leave_requests l
                JOIN employees e
                ON l.employee_id = e.employee_id
                """;

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            System.out.println(
                    rs.getInt("request_id") + " | " +
                            rs.getString("name") + " | " +
                            rs.getString("leave_type") + " | " +
                            rs.getInt("days") + " | " +
                            rs.getString("status")
            );
        }
    }
}