package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class FrmThongKe extends JFrame {
    private final String[] tenTab = {"Sách đang mượn", "Mượn quá hạn", "Theo thể loại", "Theo độc giả", "Tồn kho"};
    private final String[] cauLenh = {
        "SELECT ct.MaPM AS [Mã phiếu], dg.HoTen AS [Độc giả], s.TenSach AS [Tên sách], pm.NgayMuon AS [Ngày mượn], "
                + "pm.HanTra AS [Hạn trả] FROM ChiTietPhieuMuon ct JOIN PhieuMuon pm ON ct.MaPM = pm.MaPM "
                + "JOIN DocGia dg ON pm.MaDG = dg.MaDG JOIN Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.TinhTrang = N'Đang mượn' ORDER BY pm.HanTra, ct.MaPM, ct.MaSach",

        "SELECT dg.MaDG AS [Mã ĐG], dg.HoTen AS [Họ tên], dg.SDT AS [SĐT], s.TenSach AS [Tên sách], "
                + "pm.HanTra AS [Hạn trả], DATEDIFF(DAY, pm.HanTra, CAST(GETDATE() AS DATE)) AS [Số ngày trễ] "
                + "FROM ChiTietPhieuMuon ct JOIN PhieuMuon pm ON ct.MaPM = pm.MaPM "
                + "JOIN DocGia dg ON pm.MaDG = dg.MaDG JOIN Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.TinhTrang = N'Đang mượn' AND pm.HanTra < CAST(GETDATE() AS DATE) "
                + "ORDER BY [Số ngày trễ] DESC, s.TenSach",

        "SELECT tl.TenTL AS [Thể loại], COUNT(ct.MaSach) AS [Số lượt mượn] FROM TheLoai tl "
                + "LEFT JOIN Sach s ON s.MaTL = tl.MaTL LEFT JOIN ChiTietPhieuMuon ct ON ct.MaSach = s.MaSach "
                + "GROUP BY tl.TenTL ORDER BY [Số lượt mượn] DESC, tl.TenTL",

        "SELECT dg.MaDG AS [Mã ĐG], dg.HoTen AS [Họ tên], COUNT(ct.MaSach) AS [Số sách đã mượn], "
                + "ISNULL(SUM(ct.TienPhat), 0) AS [Tổng tiền phạt] FROM DocGia dg "
                + "LEFT JOIN PhieuMuon pm ON dg.MaDG = pm.MaDG LEFT JOIN ChiTietPhieuMuon ct ON pm.MaPM = ct.MaPM "
                + "GROUP BY dg.MaDG, dg.HoTen ORDER BY [Tổng tiền phạt] DESC, dg.MaDG",

        "SELECT MaSach AS [Mã sách], TenSach AS [Tên sách], SoLuongTong AS [Tổng], SoLuongCon AS [Còn trong kho], "
                + "ViTri AS [Vị trí] FROM Sach ORDER BY SoLuongCon, TenSach"
    };
    private final JTable[] bang = new JTable[5];

    public FrmThongKe() {
        setTitle("Thống kê");
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napTatCa();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("THỐNG KÊ", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JTabbedPane tab = new JTabbedPane();
        for (int i = 0; i < bang.length; i++) {
            bang[i] = new JTable();
            bang[i].setRowHeight(24);
            bang[i].getTableHeader().setReorderingAllowed(false);
            tab.addTab(tenTab[i], new JScrollPane(bang[i]));
        }
        JPanel giua = new JPanel(new BorderLayout());
        giua.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        giua.add(tab, BorderLayout.CENTER);
        add(giua, BorderLayout.CENTER);

        JButton btnLamMoi = new JButton("Cập nhật");
        JPanel pNut = new JPanel(new FlowLayout());
        pNut.add(btnLamMoi);
        add(pNut, BorderLayout.SOUTH);
        btnLamMoi.addActionListener(e -> napTatCa());
    }

    private void napTatCa() {
        for (int i = 0; i < bang.length; i++) {
            TienIch.napBang(bang[i], cauLenh[i]);
        }
    }
}
