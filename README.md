# Quản lý thư viện

Bài tập lớn môn Java - phần mềm quản lý thư viện viết bằng Java Swing, kết nối SQL Server qua JDBC.

Sinh viên: Bùi Minh Vượng - Lớp TH30.06 - MSV 3025131708

## Chức năng

- Quản lý sách: thêm, sửa, xóa, tìm theo tên sách / thể loại / tác giả
- Quản lý độc giả: thêm, sửa, xóa, tìm theo mã / họ tên / số điện thoại
- Quản lý thể loại: thêm, sửa, xóa, tìm
- Lập phiếu mượn: chọn độc giả, nhân viên, nhiều cuốn sách trong một phiếu
- Trả sách: trả đúng hạn hoặc trễ (tự tính phạt), báo mất sách, xóa phiếu
- Thống kê: sách đang mượn, quá hạn, theo thể loại, theo độc giả, tồn kho

## Cách chạy

1. Mở `database/QuanLyThuVien.sql` trong SSMS, bấm Execute để tạo CSDL và dữ liệu mẫu.
2. Mở file `src/main/java/quanlythuvien/KetNoi.java`, sửa `USER` và `PASS` cho đúng với SQL Server của máy.
3. Bật TCP/IP cho SQL Server (cổng 1433) và chế độ đăng nhập Mixed Mode.
4. Mở project bằng NetBeans / IntelliJ / VS Code (Maven, JDK 17 trở lên), chạy class `FrmChinh`.

Hoặc chạy bằng dòng lệnh:

    mvn compile exec:java -Dexec.mainClass=quanlythuvien.FrmChinh

## Cấu trúc

    database/                 file script SQL Server
    src/main/java/quanlythuvien/
        FrmChinh              màn hình chính
        FrmSach, FrmDocGia, FrmTheLoai, FrmMuonSach, FrmTraSach, FrmThongKe
        KetNoi                kết nối CSDL, sinh mã tự động
        TienIch, MucChon      các hàm dùng chung
