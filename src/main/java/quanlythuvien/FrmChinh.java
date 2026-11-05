package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FrmChinh extends JFrame {

    public FrmChinh() {
        setTitle("Quản lý thư viện");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel tieuDe = new JLabel("QUẢN LÝ THƯ VIỆN", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(tieuDe, BorderLayout.NORTH);

        JButton btnSach = new JButton("Quản lý sách");
        JButton btnDocGia = new JButton("Quản lý độc giả");
        JButton btnTheLoai = new JButton("Quản lý thể loại");
        JButton btnMuon = new JButton("Lập phiếu mượn");
        JButton btnTra = new JButton("Trả sách");
        JButton btnThongKe = new JButton("Thống kê");
        JButton btnThoat = new JButton("Thoát");

        JPanel pNut = new JPanel(new GridLayout(0, 1, 0, 10));
        pNut.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));
        pNut.add(btnSach);
        pNut.add(btnDocGia);
        pNut.add(btnTheLoai);
        pNut.add(btnMuon);
        pNut.add(btnTra);
        pNut.add(btnThongKe);
        pNut.add(btnThoat);
        add(pNut, BorderLayout.CENTER);

        btnSach.addActionListener(e -> new FrmSach().setVisible(true));
        btnDocGia.addActionListener(e -> new FrmDocGia().setVisible(true));
        btnTheLoai.addActionListener(e -> new FrmTheLoai().setVisible(true));
        btnMuon.addActionListener(e -> new FrmMuonSach().setVisible(true));
        btnTra.addActionListener(e -> new FrmTraSach().setVisible(true));
        btnThongKe.addActionListener(e -> new FrmThongKe().setVisible(true));
        btnThoat.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            KetNoi.getConnection().close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Không kết nối được CSDL:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new FrmChinh().setVisible(true));
    }
}
