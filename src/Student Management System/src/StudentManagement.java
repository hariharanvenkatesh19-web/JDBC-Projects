import java.sql.*;
import java.util.Scanner;

public class StudentManagement {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== STUDENT MANAGEMENT =====");
            System.out.println("1. Register Student");
            System.out.println("2. Add Course");
            System.out.println("3. Enroll Student");
            System.out.println("4. Add Attendance");
            System.out.println("5. Add Marks");
            System.out.println("6. View Students");
            System.out.println("7. Search Student");
            System.out.println("8. Generate Result");
            System.out.println("9. Delete Student");
            System.out.println("10. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try (Connection con = DBConnection.getConnection()) {

                switch (choice) {

                    case 1:
                        registerStudent(con);
                        break;

                    case 2:
                        addCourse(con);
                        break;

                    case 3:
                        enrollStudent(con);
                        break;

                    case 4:
                        addAttendance(con);
                        break;

                    case 5:
                        addMarks(con);
                        break;

                    case 6:
                        viewStudents(con);
                        break;

                    case 7:
                        searchStudent(con);
                        break;

                    case 8:
                        generateResult(con);
                        break;

                    case 9:
                        deleteStudent(con);
                        break;

                    case 10:
                        System.out.println("Program ended.");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void registerStudent(Connection con) throws Exception {

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        String sql = "INSERT INTO students(name,email,phone) VALUES(?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);

        ps.executeUpdate();

        System.out.println("Student registered successfully.");
    }

    static void addCourse(Connection con) throws Exception {

        System.out.print("Course Name: ");
        String name = sc.nextLine();

        System.out.print("Duration in months: ");
        int duration = sc.nextInt();

        String sql = "INSERT INTO courses(course_name,duration) VALUES(?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, name);
        ps.setInt(2, duration);

        ps.executeUpdate();

        System.out.println("Course added.");
    }

    static void enrollStudent(Connection con) throws Exception {

        System.out.print("Student ID: ");
        int studentId = sc.nextInt();

        System.out.print("Course ID: ");
        int courseId = sc.nextInt();

        String sql = """
                INSERT INTO enrollments
                (student_id,course_id,enrollment_date)
                VALUES(?,?,CURDATE())
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, studentId);
        ps.setInt(2, courseId);

        ps.executeUpdate();

        System.out.println("Student enrolled.");
    }

    static void addAttendance(Connection con) throws Exception {

        System.out.print("Student ID: ");
        int studentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Status (Present/Absent): ");
        String status = sc.nextLine();

        String sql = """
                INSERT INTO attendance
                (student_id,attendance_date,status)
                VALUES(?,CURDATE(),?)
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, studentId);
        ps.setString(2, status);

        ps.executeUpdate();

        System.out.println("Attendance added.");
    }

    static void addMarks(Connection con) throws Exception {

        System.out.print("Student ID: ");
        int studentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Subject: ");
        String subject = sc.nextLine();

        System.out.print("Marks: ");
        int marks = sc.nextInt();

        String sql =
                "INSERT INTO marks(student_id,subject,marks) VALUES(?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, studentId);
        ps.setString(2, subject);
        ps.setInt(3, marks);

        ps.executeUpdate();

        System.out.println("Marks added.");
    }

    static void viewStudents(Connection con) throws Exception {

        String sql = "SELECT * FROM students";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            System.out.println(
                    rs.getInt("student_id") + " | " +
                            rs.getString("name") + " | " +
                            rs.getString("email") + " | " +
                            rs.getString("phone")
            );
        }
    }

    static void searchStudent(Connection con) throws Exception {

        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        String sql =
                "SELECT * FROM students WHERE name LIKE ?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, "%" + name + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            System.out.println(
                    rs.getInt("student_id") + " - " +
                            rs.getString("name") + " - " +
                            rs.getString("email")
            );
        }
    }

    static void generateResult(Connection con) throws Exception {

        System.out.print("Student ID: ");
        int studentId = sc.nextInt();

        String sql = """
                SELECT s.name,
                       c.course_name,
                       AVG(m.marks) AS average_marks
                FROM students s
                JOIN enrollments e
                    ON s.student_id = e.student_id
                JOIN courses c
                    ON e.course_id = c.course_id
                JOIN marks m
                    ON s.student_id = m.student_id
                WHERE s.student_id = ?
                GROUP BY s.name,c.course_name
                """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, studentId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            double avg = rs.getDouble("average_marks");

            System.out.println("\nStudent : " +
                    rs.getString("name"));

            System.out.println("Course : " +
                    rs.getString("course_name"));

            System.out.println("Average : " + avg);

            System.out.println(
                    avg >= 40 ? "Result : PASS" : "Result : FAIL"
            );
        }
    }

    static void deleteStudent(Connection con) throws Exception {

        System.out.print("Student ID: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM students WHERE student_id=?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("Student deleted.");
    }
}