/* =====================================================================
   DE TAI  : QUAN LY THU VIEN
   CSDL    : QuanLyThuVien (SQL Server 2017 tro len)
   Chay    : mo SSMS, New Query, dan toan bo file nay va Execute (F5)
   ===================================================================== */

USE master;
GO

IF DB_ID(N'QuanLyThuVien') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyThuVien SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyThuVien;
END
GO

CREATE DATABASE QuanLyThuVien COLLATE Vietnamese_CI_AS;
GO

USE QuanLyThuVien;
GO

/* ---------- 1. Cac bang danh muc ---------- */

CREATE TABLE TheLoai (
    MaTL    CHAR(5)        NOT NULL,
    TenTL   NVARCHAR(100)  NOT NULL,
    MoTa    NVARCHAR(255)  NULL,
    CONSTRAINT PK_TheLoai PRIMARY KEY (MaTL),
    CONSTRAINT UQ_TheLoai_TenTL UNIQUE (TenTL)
);
GO

CREATE TABLE TacGia (
    MaTG      CHAR(5)       NOT NULL,
    TenTG     NVARCHAR(100) NOT NULL,
    NamSinh   INT           NULL,
    QuocTich  NVARCHAR(50)  NULL,
    CONSTRAINT PK_TacGia PRIMARY KEY (MaTG),
    CONSTRAINT CK_TacGia_NamSinh CHECK (NamSinh IS NULL OR NamSinh BETWEEN 1000 AND 2100)
);
GO

CREATE TABLE NhaXuatBan (
    MaNXB   CHAR(5)        NOT NULL,
    TenNXB  NVARCHAR(150)  NOT NULL,
    DiaChi  NVARCHAR(200)  NULL,
    SDT     VARCHAR(15)    NULL,
    CONSTRAINT PK_NhaXuatBan PRIMARY KEY (MaNXB),
    CONSTRAINT UQ_NhaXuatBan_TenNXB UNIQUE (TenNXB)
);
GO

/* ---------- 2. Sach va bang noi sach - tac gia ---------- */

CREATE TABLE Sach (
    MaSach       CHAR(6)        NOT NULL,
    TenSach      NVARCHAR(200)  NOT NULL,
    MaTL         CHAR(5)        NOT NULL,
    MaNXB        CHAR(5)        NOT NULL,
    NamXB        INT            NULL,
    SoTrang      INT            NULL,
    GiaBia       DECIMAL(12,0)  NOT NULL CONSTRAINT DF_Sach_GiaBia DEFAULT 0,
    SoLuongTong  INT            NOT NULL CONSTRAINT DF_Sach_SLTong DEFAULT 0,
    SoLuongCon   INT            NOT NULL CONSTRAINT DF_Sach_SLCon  DEFAULT 0,
    ViTri        NVARCHAR(50)   NULL,
    CONSTRAINT PK_Sach PRIMARY KEY (MaSach),
    CONSTRAINT FK_Sach_TheLoai    FOREIGN KEY (MaTL)  REFERENCES TheLoai(MaTL),
    CONSTRAINT FK_Sach_NhaXuatBan FOREIGN KEY (MaNXB) REFERENCES NhaXuatBan(MaNXB),
    CONSTRAINT CK_Sach_NamXB   CHECK (NamXB IS NULL OR NamXB BETWEEN 1900 AND 2100),
    CONSTRAINT CK_Sach_SoTrang CHECK (SoTrang IS NULL OR SoTrang > 0),
    CONSTRAINT CK_Sach_GiaBia  CHECK (GiaBia >= 0),
    CONSTRAINT CK_Sach_SLTong  CHECK (SoLuongTong >= 0),
    CONSTRAINT CK_Sach_SLCon   CHECK (SoLuongCon >= 0 AND SoLuongCon <= SoLuongTong)
);
GO

CREATE TABLE SachTacGia (
    MaSach  CHAR(6) NOT NULL,
    MaTG    CHAR(5) NOT NULL,
    CONSTRAINT PK_SachTacGia PRIMARY KEY (MaSach, MaTG),
    CONSTRAINT FK_SachTacGia_Sach   FOREIGN KEY (MaSach) REFERENCES Sach(MaSach)
        ON DELETE CASCADE,
    CONSTRAINT FK_SachTacGia_TacGia FOREIGN KEY (MaTG)   REFERENCES TacGia(MaTG)
);
GO

/* ---------- 3. Doc gia, nhan vien ---------- */

CREATE TABLE DocGia (
    MaDG        CHAR(6)        NOT NULL,
    HoTen       NVARCHAR(100)  NOT NULL,
    NgaySinh    DATE           NULL,
    GioiTinh    NVARCHAR(3)    NOT NULL,
    DiaChi      NVARCHAR(200)  NULL,
    SDT         VARCHAR(15)    NOT NULL,
    Email       VARCHAR(100)   NULL,
    NgayLapThe  DATE           NOT NULL
                   CONSTRAINT DF_DocGia_NgayLapThe DEFAULT (CAST(GETDATE() AS DATE)),
    NgayHetHan  DATE           NOT NULL,
    TrangThai   NVARCHAR(20)   NOT NULL
                   CONSTRAINT DF_DocGia_TrangThai DEFAULT N'Hoạt động',
    CONSTRAINT PK_DocGia PRIMARY KEY (MaDG),
    CONSTRAINT UQ_DocGia_SDT UNIQUE (SDT),
    CONSTRAINT CK_DocGia_GioiTinh  CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    CONSTRAINT CK_DocGia_Email     CHECK (Email IS NULL OR Email LIKE '%_@_%._%'),
    CONSTRAINT CK_DocGia_HanThe    CHECK (NgayHetHan > NgayLapThe),
    CONSTRAINT CK_DocGia_TrangThai CHECK
        (TrangThai IN (N'Hoạt động', N'Tạm khóa', N'Hết hạn'))
);
GO

CREATE TABLE NhanVien (
    MaNV        CHAR(5)        NOT NULL,
    HoTen       NVARCHAR(100)  NOT NULL,
    GioiTinh    NVARCHAR(3)    NOT NULL,
    ChucVu      NVARCHAR(50)   NOT NULL
                   CONSTRAINT DF_NhanVien_ChucVu DEFAULT N'Thủ thư',
    SDT         VARCHAR(15)    NULL,
    NgayVaoLam  DATE           NOT NULL
                   CONSTRAINT DF_NhanVien_NgayVaoLam DEFAULT (CAST(GETDATE() AS DATE)),
    CONSTRAINT PK_NhanVien PRIMARY KEY (MaNV),
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    CONSTRAINT CK_NhanVien_ChucVu   CHECK (ChucVu IN (N'Thủ thư', N'Quản lý'))
);
GO

/* ---------- 4. Muon - tra ---------- */

CREATE TABLE PhieuMuon (
    MaPM      CHAR(7)        NOT NULL,
    MaDG      CHAR(6)        NOT NULL,
    MaNV      CHAR(5)        NOT NULL,
    NgayMuon  DATE           NOT NULL
                 CONSTRAINT DF_PhieuMuon_NgayMuon DEFAULT (CAST(GETDATE() AS DATE)),
    HanTra    DATE           NOT NULL,
    GhiChu    NVARCHAR(255)  NULL,
    CONSTRAINT PK_PhieuMuon PRIMARY KEY (MaPM),
    CONSTRAINT FK_PhieuMuon_DocGia   FOREIGN KEY (MaDG) REFERENCES DocGia(MaDG),
    CONSTRAINT FK_PhieuMuon_NhanVien FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV),
    CONSTRAINT CK_PhieuMuon_HanTra   CHECK
        (HanTra > NgayMuon AND DATEDIFF(DAY, NgayMuon, HanTra) <= 30)
);
GO

CREATE TABLE ChiTietPhieuMuon (
    MaPM       CHAR(7)       NOT NULL,
    MaSach     CHAR(6)       NOT NULL,
    NgayTra    DATE          NULL,
    TinhTrang  NVARCHAR(20)  NOT NULL
               CONSTRAINT DF_CTPM_TinhTrang DEFAULT N'Đang mượn',
    TienPhat   DECIMAL(12,0) NOT NULL CONSTRAINT DF_CTPM_TienPhat DEFAULT 0,
    CONSTRAINT PK_ChiTietPhieuMuon PRIMARY KEY (MaPM, MaSach),
    CONSTRAINT FK_CTPM_PhieuMuon FOREIGN KEY (MaPM)   REFERENCES PhieuMuon(MaPM)
        ON DELETE CASCADE,
    CONSTRAINT FK_CTPM_Sach      FOREIGN KEY (MaSach) REFERENCES Sach(MaSach),
    CONSTRAINT CK_CTPM_TinhTrang CHECK
        (TinhTrang IN (N'Đang mượn', N'Đã trả', N'Mất sách')),
    CONSTRAINT CK_CTPM_TienPhat  CHECK (TienPhat >= 0),
    CONSTRAINT CK_CTPM_NgayTra   CHECK (
        (TinhTrang = N'Đang mượn' AND NgayTra IS NULL)
        OR (TinhTrang <> N'Đang mượn' AND NgayTra IS NOT NULL)
    )
);
GO

/* Index cho cac cot hay dung de tim kiem */
CREATE INDEX IX_Sach_TenSach       ON Sach(TenSach);
CREATE INDEX IX_Sach_MaTL          ON Sach(MaTL);
CREATE INDEX IX_DocGia_HoTen       ON DocGia(HoTen);
CREATE INDEX IX_PhieuMuon_MaDG     ON PhieuMuon(MaDG);
CREATE INDEX IX_CTPM_MaSach        ON ChiTietPhieuMuon(MaSach);
GO

/* =====================================================================
   TRIGGER, VIEW, STORED PROCEDURE
   ===================================================================== */

/* Trigger 1: cho muon sach thi tru so luong con trong kho */
CREATE TRIGGER trg_CTPM_Insert ON ChiTietPhieuMuon
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dem TABLE (MaSach CHAR(6) PRIMARY KEY, SL INT);
    INSERT INTO @dem
    SELECT MaSach, COUNT(*) FROM inserted
    WHERE TinhTrang = N'Đang mượn'
    GROUP BY MaSach;

    IF EXISTS (SELECT 1 FROM Sach s JOIN @dem d ON s.MaSach = d.MaSach
               WHERE s.SoLuongCon < d.SL)
    BEGIN
        RAISERROR(N'Sách không còn đủ số lượng trong kho để cho mượn.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    UPDATE s SET s.SoLuongCon = s.SoLuongCon - d.SL
    FROM Sach s JOIN @dem d ON s.MaSach = d.MaSach;
END
GO

/* Trigger 2: doc gia tra sach thi cong lai kho, lam mat sach thi giam tong so */
CREATE TRIGGER trg_CTPM_Update ON ChiTietPhieuMuon
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT UPDATE(TinhTrang) RETURN;

    UPDATE s SET s.SoLuongCon = s.SoLuongCon + x.SL
    FROM Sach s
    JOIN (SELECT i.MaSach, COUNT(*) AS SL
          FROM inserted i JOIN deleted d ON i.MaPM = d.MaPM AND i.MaSach = d.MaSach
          WHERE d.TinhTrang = N'Đang mượn' AND i.TinhTrang = N'Đã trả'
          GROUP BY i.MaSach) x ON s.MaSach = x.MaSach;

    UPDATE s SET s.SoLuongTong = s.SoLuongTong - x.SL
    FROM Sach s
    JOIN (SELECT i.MaSach, COUNT(*) AS SL
          FROM inserted i JOIN deleted d ON i.MaPM = d.MaPM AND i.MaSach = d.MaSach
          WHERE d.TinhTrang = N'Đang mượn' AND i.TinhTrang = N'Mất sách'
          GROUP BY i.MaSach) x ON s.MaSach = x.MaSach;
END
GO

/* View: thong tin sach day du (the loai, NXB, cac tac gia) */
CREATE VIEW vw_ThongTinSach
AS
SELECT s.MaSach, s.TenSach, tl.TenTL, nxb.TenNXB, s.NamXB,
       s.SoLuongTong, s.SoLuongCon,
       STRING_AGG(tg.TenTG, N', ') WITHIN GROUP (ORDER BY tg.TenTG) AS TacGia
FROM Sach s
JOIN TheLoai tl     ON s.MaTL = tl.MaTL
JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB
LEFT JOIN SachTacGia stg ON s.MaSach = stg.MaSach
LEFT JOIN TacGia tg      ON stg.MaTG = tg.MaTG
GROUP BY s.MaSach, s.TenSach, tl.TenTL, nxb.TenNXB, s.NamXB,
         s.SoLuongTong, s.SoLuongCon;
GO

/* Procedure: tra sach, tu tinh tien phat 1.000 dong / ngay tre */
CREATE PROCEDURE sp_TraSach
    @MaPM    CHAR(7),
    @MaSach  CHAR(6),
    @NgayTra DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;
    IF @NgayTra IS NULL SET @NgayTra = CAST(GETDATE() AS DATE);

    DECLARE @HanTra DATE, @TienPhat DECIMAL(12,0) = 0;

    SELECT @HanTra = pm.HanTra
    FROM PhieuMuon pm
    JOIN ChiTietPhieuMuon ct ON pm.MaPM = ct.MaPM
    WHERE ct.MaPM = @MaPM AND ct.MaSach = @MaSach AND ct.TinhTrang = N'Đang mượn';

    IF @HanTra IS NULL
    BEGIN
        RAISERROR(N'Không tìm thấy sách đang mượn trong phiếu này.', 16, 1);
        RETURN;
    END

    IF @NgayTra > @HanTra
        SET @TienPhat = DATEDIFF(DAY, @HanTra, @NgayTra) * 1000;

    UPDATE ChiTietPhieuMuon
    SET TinhTrang = N'Đã trả', NgayTra = @NgayTra, TienPhat = @TienPhat
    WHERE MaPM = @MaPM AND MaSach = @MaSach;
END
GO

/* =====================================================================
   DU LIEU MAU (chay sau khi da tao trigger de so luong ton kho tu cap nhat)
   ===================================================================== */

INSERT INTO TheLoai (MaTL, TenTL, MoTa) VALUES
    ('TL001', N'Văn học Việt Nam', N'Tiểu thuyết, truyện ngắn, tản văn của tác giả trong nước'),
    ('TL002', N'Văn học nước ngoài', N'Sách văn học dịch'),
    ('TL003', N'Khoa học - Công nghệ', N'Sách khoa học tự nhiên và kỹ thuật'),
    ('TL004', N'Tin học', N'Lập trình, cơ sở dữ liệu, mạng máy tính'),
    ('TL005', N'Kinh tế', N'Kinh doanh, quản trị, kỹ năng làm việc'),
    ('TL006', N'Thiếu nhi', N'Truyện và sách dành cho lứa tuổi nhỏ'),
    ('TL007', N'Lịch sử', N'Lịch sử Việt Nam và thế giới');
GO

INSERT INTO TacGia (MaTG, TenTG, NamSinh, QuocTich) VALUES
    ('TG001', N'Tô Hoài', 1920, N'Việt Nam'),
    ('TG002', N'Nguyễn Nhật Ánh', 1955, N'Việt Nam'),
    ('TG003', 'Nam Cao', 1915, N'Việt Nam'),
    ('TG004', 'Paulo Coelho', 1947, 'Brazil'),
    ('TG005', 'Robert C. Martin', 1952, N'Hoa Kỳ'),
    ('TG006', 'Abraham Silberschatz', NULL, N'Hoa Kỳ'),
    ('TG007', 'Henry F. Korth', NULL, N'Hoa Kỳ'),
    ('TG008', 'S. Sudarshan', NULL, N'Ấn Độ'),
    ('TG009', 'Dale Carnegie', 1888, N'Hoa Kỳ'),
    ('TG010', 'Yuval Noah Harari', 1976, 'Israel');
GO

INSERT INTO NhaXuatBan (MaNXB, TenNXB, DiaChi, SDT) VALUES
    ('NXB01', N'NXB Kim Đồng', N'Hà Nội', '0240000001'),
    ('NXB02', N'NXB Trẻ', N'TP. Hồ Chí Minh', '0280000002'),
    ('NXB03', N'NXB Văn học', N'Hà Nội', '0240000003'),
    ('NXB04', N'NXB Hội Nhà văn', N'Hà Nội', '0240000004'),
    ('NXB05', N'NXB Thanh Niên', N'Hà Nội', '0240000005'),
    ('NXB06', N'NXB Giáo dục Việt Nam', N'Hà Nội', '0240000006'),
    ('NXB07', N'NXB Tổng hợp TP.HCM', N'TP. Hồ Chí Minh', '0280000007'),
    ('NXB08', N'NXB Thế Giới', N'Hà Nội', '0240000008');
GO

INSERT INTO Sach (MaSach, TenSach, MaTL, MaNXB, NamXB, SoTrang, GiaBia, SoLuongTong, SoLuongCon, ViTri) VALUES
    ('S00001', N'Dế Mèn phiêu lưu ký', 'TL006', 'NXB01', 2019, 144, 48000, 8, 8, N'Kệ B1'),
    ('S00002', N'Cho tôi xin một vé đi tuổi thơ', 'TL001', 'NXB02', 2018, 208, 80000, 10, 10, N'Kệ A1'),
    ('S00003', N'Chí Phèo', 'TL001', 'NXB03', 2017, 120, 45000, 6, 6, N'Kệ A1'),
    ('S00004', N'Nhà giả kim', 'TL002', 'NXB04', 2020, 227, 79000, 7, 7, N'Kệ A2'),
    ('S00005', 'Clean Code', 'TL004', 'NXB05', 2019, 464, 220000, 4, 4, N'Kệ C1'),
    ('S00006', 'Database System Concepts', 'TL004', 'NXB06', 2019, 1376, 450000, 4, 4, N'Kệ C1'),
    ('S00007', N'Đắc nhân tâm', 'TL005', 'NXB07', 2016, 320, 86000, 9, 9, N'Kệ D1'),
    ('S00008', N'Sapiens: Lược sử loài người', 'TL007', 'NXB08', 2020, 560, 189000, 5, 5, N'Kệ E1'),
    ('S00009', N'Tôi thấy hoa vàng trên cỏ xanh', 'TL001', 'NXB02', 2018, 378, 125000, 10, 10, N'Kệ A1'),
    ('S00010', N'Mắt biếc', 'TL001', 'NXB02', 2019, 300, 110000, 8, 8, N'Kệ A1');
GO

INSERT INTO SachTacGia (MaSach, MaTG) VALUES
    ('S00001', 'TG001'),
    ('S00002', 'TG002'),
    ('S00003', 'TG003'),
    ('S00004', 'TG004'),
    ('S00005', 'TG005'),
    ('S00006', 'TG006'),
    ('S00006', 'TG007'),
    ('S00006', 'TG008'),
    ('S00007', 'TG009'),
    ('S00008', 'TG010'),
    ('S00009', 'TG002'),
    ('S00010', 'TG002');
GO

INSERT INTO NhanVien (MaNV, HoTen, GioiTinh, ChucVu, SDT, NgayVaoLam) VALUES
    ('NV001', N'Nguyễn Thị Lan', N'Nữ', N'Thủ thư', '0901000001', '2023-02-01'),
    ('NV002', N'Trần Văn Hùng', 'Nam', N'Thủ thư', '0901000002', '2024-06-15'),
    ('NV003', N'Lê Minh Châu', N'Nữ', N'Quản lý', '0901000003', '2020-09-01');
GO

INSERT INTO DocGia (MaDG, HoTen, NgaySinh, GioiTinh, DiaChi, SDT, Email, NgayLapThe, NgayHetHan, TrangThai) VALUES
    ('DG0001', N'Phạm Quang Huy', '2004-03-12', 'Nam', N'Cầu Giấy, Hà Nội', '0912000001', 'huy.pq@example.com', '2026-09-01', '2027-09-01', N'Hoạt động'),
    ('DG0002', N'Nguyễn Thu Hà', '2005-07-21', N'Nữ', N'Hà Đông, Hà Nội', '0912000002', 'ha.nt@example.com', '2026-09-01', '2027-09-01', N'Hoạt động'),
    ('DG0003', N'Đỗ Văn Nam', '2003-11-02', 'Nam', N'Thanh Xuân, Hà Nội', '0912000003', 'nam.dv@example.com', '2026-09-20', '2027-09-20', N'Hoạt động'),
    ('DG0004', N'Vũ Thị Mai', '2004-01-30', N'Nữ', N'Long Biên, Hà Nội', '0912000004', 'mai.vt@example.com', '2026-09-10', '2027-09-10', N'Hoạt động'),
    ('DG0005', N'Lê Hoàng Long', '2002-05-17', 'Nam', N'Nam Từ Liêm, Hà Nội', '0912000005', NULL, '2026-02-01', '2027-02-01', N'Tạm khóa'),
    ('DG0006', N'Trần Bảo Ngọc', '2005-09-09', N'Nữ', N'Đống Đa, Hà Nội', '0912000006', 'ngoc.tb@example.com', '2026-10-01', '2027-10-01', N'Hoạt động');
GO

INSERT INTO PhieuMuon (MaPM, MaDG, MaNV, NgayMuon, HanTra, GhiChu) VALUES
    ('PM00001', 'DG0001', 'NV001', '2026-09-10', '2026-09-24', NULL),
    ('PM00002', 'DG0002', 'NV002', '2026-09-15', '2026-09-29', NULL),
    ('PM00003', 'DG0003', 'NV001', '2026-10-05', '2026-10-19', NULL),
    ('PM00004', 'DG0004', 'NV002', '2026-10-12', '2026-10-26', NULL),
    ('PM00005', 'DG0001', 'NV003', '2026-10-20', '2026-11-03', N'Mượn thêm 1 cuốn'),
    ('PM00006', 'DG0005', 'NV001', '2026-07-10', '2026-07-24', NULL);
GO

INSERT INTO ChiTietPhieuMuon (MaPM, MaSach, NgayTra, TinhTrang, TienPhat) VALUES
    ('PM00001', 'S00005', '2026-09-22', N'Đã trả', 0),
    ('PM00001', 'S00006', '2026-09-24', N'Đã trả', 0),
    ('PM00002', 'S00002', '2026-10-03', N'Đã trả', 4000),
    ('PM00003', 'S00004', NULL, N'Đang mượn', 0),
    ('PM00003', 'S00009', NULL, N'Đang mượn', 0),
    ('PM00004', 'S00008', NULL, N'Đang mượn', 0),
    ('PM00004', 'S00010', '2026-10-20', N'Đã trả', 0),
    ('PM00005', 'S00007', NULL, N'Đang mượn', 0),
    ('PM00006', 'S00003', NULL, N'Đang mượn', 0);
GO

/* Sach PM00006 bi bao mat -> trigger tu giam SoLuongTong */
UPDATE ChiTietPhieuMuon
SET TinhTrang = N'Mất sách', NgayTra = '2026-08-05', TienPhat = 90000
WHERE MaPM = 'PM00006' AND MaSach = 'S00003';
GO

/* =====================================================================
   CAC CAU TRUY VAN MINH HOA
   ===================================================================== */

-- Q1. Sach dang duoc muon (chua tra) va nguoi muon
SELECT ct.MaPM, dg.HoTen AS DocGia, s.TenSach, pm.NgayMuon, pm.HanTra
FROM ChiTietPhieuMuon ct
JOIN PhieuMuon pm ON ct.MaPM = pm.MaPM
JOIN DocGia dg    ON pm.MaDG = dg.MaDG
JOIN Sach s       ON ct.MaSach = s.MaSach
WHERE ct.TinhTrang = N'Đang mượn'
ORDER BY pm.HanTra, ct.MaPM, ct.MaSach;
GO

-- Q2. Doc gia muon qua han tinh den ngay 26/10/2026
DECLARE @HomNay DATE = '2026-10-26';
SELECT dg.MaDG, dg.HoTen, dg.SDT, s.TenSach, pm.HanTra,
       DATEDIFF(DAY, pm.HanTra, @HomNay) AS SoNgayTre
FROM ChiTietPhieuMuon ct
JOIN PhieuMuon pm ON ct.MaPM = pm.MaPM
JOIN DocGia dg    ON pm.MaDG = dg.MaDG
JOIN Sach s       ON ct.MaSach = s.MaSach
WHERE ct.TinhTrang = N'Đang mượn' AND pm.HanTra < @HomNay
ORDER BY SoNgayTre DESC, s.TenSach;
GO

-- Q3. So luot muon theo the loai
SELECT tl.TenTL, COUNT(*) AS SoLuotMuon
FROM ChiTietPhieuMuon ct
JOIN Sach s     ON ct.MaSach = s.MaSach
JOIN TheLoai tl ON s.MaTL = tl.MaTL
GROUP BY tl.TenTL
ORDER BY SoLuotMuon DESC, tl.TenTL;
GO

-- Q4. Thong ke tung doc gia: so sach da muon va tong tien phat
SELECT dg.MaDG, dg.HoTen,
       COUNT(ct.MaSach)           AS SoSachDaMuon,
       ISNULL(SUM(ct.TienPhat), 0) AS TongTienPhat
FROM DocGia dg
LEFT JOIN PhieuMuon pm        ON dg.MaDG = pm.MaDG
LEFT JOIN ChiTietPhieuMuon ct ON pm.MaPM = ct.MaPM
GROUP BY dg.MaDG, dg.HoTen
ORDER BY TongTienPhat DESC, dg.MaDG;
GO

-- Q5. Cac sach cua tac gia Nguyen Nhat Anh
SELECT s.MaSach, s.TenSach, s.NamXB, nxb.TenNXB
FROM Sach s
JOIN SachTacGia stg ON s.MaSach = stg.MaSach
JOIN TacGia tg      ON stg.MaTG = tg.MaTG
JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB
WHERE tg.TenTG = N'Nguyễn Nhật Ánh'
ORDER BY s.NamXB, s.TenSach;
GO

-- Q6. Dung view: sach co nhieu tac gia
SELECT MaSach, TenSach, TacGia, SoLuongCon
FROM vw_ThongTinSach
WHERE TacGia LIKE N'%Silberschatz%';
GO

/* ---------- Thu procedure + trigger tra sach ---------- */
SELECT MaSach, TenSach, SoLuongTong, SoLuongCon FROM Sach WHERE MaSach = 'S00004';
EXEC sp_TraSach 'PM00003', 'S00004', '2026-10-26';
SELECT MaSach, TenSach, SoLuongTong, SoLuongCon FROM Sach WHERE MaSach = 'S00004';
SELECT MaPM, MaSach, NgayTra, TinhTrang, TienPhat
FROM ChiTietPhieuMuon WHERE MaPM = 'PM00003';
GO

/* ---------- Thu vi pham rang buoc (se bao loi - dung de chung minh) ---------- */
-- a) CHECK: so luong con lon hon tong so
-- INSERT INTO Sach (MaSach, TenSach, MaTL, MaNXB, SoLuongTong, SoLuongCon)
-- VALUES ('S00099', N'Sách thử', 'TL001', 'NXB01', 5, 8);

-- b) FOREIGN KEY: the loai khong ton tai
-- INSERT INTO Sach (MaSach, TenSach, MaTL, MaNXB, SoLuongTong, SoLuongCon)
-- VALUES ('S00098', N'Sách thử', 'TL999', 'NXB01', 5, 5);

-- c) Xoa the loai dang co sach -> loi FK_Sach_TheLoai
-- DELETE FROM TheLoai WHERE MaTL = 'TL001';
