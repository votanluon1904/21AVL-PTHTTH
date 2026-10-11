package JavaJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuan bi moi truong va thiet lap ket noi
            Class.forName("org.sqlite.JDBC");

            // File nhanvien.db se duoc tao tai thu muc hien tai (JavaJDBC)
            String url = "jdbc:sqlite:JavaJDBC/nhanvien.db";
            conn = DriverManager.getConnection(url);

            // Tat auto-commit de quan ly Transaction (Bai 2)
            conn.setAutoCommit(false);

            // B2. Tao doi tuong Statement tu Connection
            stmt = conn.createStatement();

            // Xoa bang cu neu chuong trinh duoc chay lai nhieu lan de tranh loi trung du
            // lieu
            stmt.executeUpdate("DROP TABLE IF EXISTS SanPham");
            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien");

            // BAI TAP 1 & 4: QUAN LY DANH MUC SAN PHAM (Dung PreparedStatement)
            System.out.println("=== 1. QUAN LY DANH MUC SAN PHAM ===");

            // Tao bang SanPham
            String sqlCreateSP = "CREATE TABLE SanPham ("
                    + "maSP TEXT PRIMARY KEY, "
                    + "tenSP TEXT NOT NULL, "
                    + "gia REAL, "
                    + "soluong INTEGER"
                    + ");";
            stmt.executeUpdate(sqlCreateSP);

            // CREATE (Them moi bang PreparedStatement)
            String sqlInsertSP = "INSERT INTO SanPham(maSP, tenSP, gia, soluong) VALUES(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlInsertSP);

            // Them SP1, SP2, SP3
            pstmt.setString(1, "SP01");
            pstmt.setString(2, "Laptop Dell");
            pstmt.setDouble(3, 15000000);
            pstmt.setInt(4, 10);
            pstmt.executeUpdate();
            pstmt.setString(1, "SP02");
            pstmt.setString(2, "Chuot khong day");
            pstmt.setDouble(3, 250000);
            pstmt.setInt(4, 50);
            pstmt.executeUpdate();
            pstmt.setString(1, "SP03");
            pstmt.setString(2, "Ban phim co");
            pstmt.setDouble(3, 850000);
            pstmt.setInt(4, 20);
            pstmt.executeUpdate();
            System.out.println("- Da them 3 san pham thanh cong.");

            // READ (Truy van) - Hien thi tat ca san pham
            System.out.println("\n--- Danh sach san pham ---");
            rs = stmt.executeQuery("SELECT * FROM SanPham");
            while (rs.next()) {
                System.out.printf("Ma: %s | Ten: %s | Gia: %,.0f VND | SL: %d\n",
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getDouble("gia"),
                        rs.getInt("soluong"));
            }
            rs.close();

            // UPDATE (Cap nhat gia va so luong san pham SP02)
            String sqlUpdateSP = "UPDATE SanPham SET gia = ?, soluong = ? WHERE maSP = ?";
            pstmt = conn.prepareStatement(sqlUpdateSP);
            pstmt.setDouble(1, 220000);
            pstmt.setInt(2, 45);
            pstmt.setString(3, "SP02");
            pstmt.executeUpdate();
            System.out.println("\n- Da cap nhat thong tin cho san pham SP02.");

            // DELETE (Xoa san pham SP03)
            String sqlDeleteSP = "DELETE FROM SanPham WHERE maSP = ?";
            pstmt = conn.prepareStatement(sqlDeleteSP);
            pstmt.setString(1, "SP03");
            pstmt.executeUpdate();
            System.out.println("- Da xoa san pham SP03.");

            // BAI B3, B4 & BAI 5: QUAN LY NHAN VIEN & TRUY VAN NANG CAO

            System.out.println("\n=== 2. QUAN LY NHAN VIEN KEM THONG KE ===");

            // B3.1. Tao bang NhanVien
            String sqlCreateNV = "CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT, "
                    + "luong REAL"
                    + ");";
            stmt.executeUpdate(sqlCreateNV);

            // B3.2. Them du lieu nhan vien
            String sqlInsertNV = "INSERT INTO NhanVien(id, ten, chuc_vu, luong) VALUES(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlInsertNV);

            Object[][] dataNV = {
                    { 1, "Nguyen Van A", "Giam doc", 50000000 },
                    { 2, "Tran Thi B", "Truong phong", 30000000 },
                    { 3, "Le Van C", "Truong phong", 28000000 },
                    { 4, "Pham Van D", "Nhan vien", 15000000 },
                    { 5, "Vu Thi E", "Nhan vien", 12000000 }
            };
            for (Object[] row : dataNV) {
                pstmt.setInt(1, (Integer) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setDouble(4, ((Number) row[3]).doubleValue());
                pstmt.executeUpdate();
            }
            System.out.println("- Da them 5 nhan vien vao he thong.");

            // B4. Truy van va hien thi du lieu nhan vien
            System.out.println("\n--- Danh sach nhan vien hien tai ---");
            rs = stmt.executeQuery("SELECT * FROM NhanVien");
            while (rs.next()) {
                // Su dung printf de fomat toan bo chuoi
                System.out.printf("ID: %d | Ten: %s | Chuc vu: %s | Luong: %,.0f VND\n",
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("chuc_vu"),
                        rs.getDouble("luong"));
            }
            rs.close();

            // BAI 5: Truy van nang cao (SUM, AVG, MIN, MAX)
            System.out.println("\n--- THONG KE LUONG ---");
            String sqlThongKeLuong = "SELECT SUM(luong) AS tong, AVG(luong) AS tb, MAX(luong) AS cao_nhat, MIN(luong) AS thap_nhat FROM NhanVien";
            rs = stmt.executeQuery(sqlThongKeLuong);
            if (rs.next()) {
                // Su dung printf va %,.0f de them dau phay ngan cach hang nghin
                System.out.printf("Tong quy luong            : %,.0f VND\n", rs.getDouble("tong"));
                System.out.printf("Muc luong trung binh      : %,.0f VND\n", rs.getDouble("tb"));
                System.out.printf("Nhan vien luong cao nhat  : %,.0f VND\n", rs.getDouble("cao_nhat"));
                System.out.printf("Nhan vien luong thap nhat : %,.0f VND\n", rs.getDouble("thap_nhat"));
            }
            rs.close();

            // BAI 5: Dem so luong nhan vien theo chuc vu (COUNT va GROUP BY)
            System.out.println("\n--- SO LUONG NHAN VIEN THEO CHUC VU ---");
            String sqlDemChucVu = "SELECT chuc_vu, COUNT(*) AS so_luong FROM NhanVien GROUP BY chuc_vu";
            rs = stmt.executeQuery(sqlDemChucVu);
            while (rs.next()) {
                System.out.println("Chuc vu " + rs.getString("chuc_vu") + ": " + rs.getInt("so_luong") + " nguoi");
            }
            rs.close();

            // KET THUC GIAO DICH: Luu toan bo du lieu
            conn.commit();
            System.out.println("\n[He thong] Giao dich hoan tat! Da luu toan bo du lieu vao nhanvien.db.");

        } catch (ClassNotFoundException e) {
            System.out.println("\n[Loi] Khong tim thay JDBC Driver: " + e.getMessage());
        } catch (SQLException e) {
            // XU LY NGOAI LE (Bai 3)
            System.out.println("\n[Loi SQL] Co loi xay ra trong qua trinh truy van: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[He thong] Da thuc hien ROLLBACK de huy tat ca thay doi do loi!");
                } catch (SQLException ex) {
                    System.out.println("[Loi] Khong the rollback: " + ex.getMessage());
                }
            }
        } finally {
            // B5. Dong ket noi va giai phong tai nguyen theo thu tu nguoc lai
            System.out.println("\n[He thong] Dang don dep tai nguyen...");
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
                System.out.println("[He thong] Da dong moi ket noi an toan.");
            } catch (SQLException e) {
                System.out.println("[Loi] Loi khi dong ket noi: " + e.getMessage());
            }
        }
    }
}