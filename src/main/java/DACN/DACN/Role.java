package DACN.DACN;
//TD79-Role
import lombok.AllArgsConstructor;


public enum Role {
    ADMIN(1), // Vai trò quản trị viên, có quyền cao nhất trong hệ thống.
    USER(2); // Vai trò người dùng bình thường, có quyền hạn giới hạn.
    public final long value; // Biến này lưu giá trị số tương ứng với mỗi vai trò.
    // Constructor cho enum
    Role(long value) {
        this.value = value;
    }
    public long getValue() {
        return value;
    }
}