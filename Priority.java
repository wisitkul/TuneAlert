/**
 * Priority — ระดับความสำคัญของการแจ้งเตือน
 *
 * enum ใน Java เป็น class เต็มตัว: มี field, constructor และ method ได้
 * ใช้แทน int constants เพื่อความ type-safe (ส่งเลขมั่ว ๆ เข้ามาไม่ได้)
 *
 * ภารกิจ: เพิ่ม field `level` ให้แต่ละค่า — LOW=1, NORMAL=2, URGENT=3
 * (ดูตัวอย่าง enum Planet ในสไลด์เป็นแนวทาง)
 */
public enum Priority {

    // TODO(2.1): ผูกตัวเลขระดับเข้ากับแต่ละค่า เช่น LOW(1), ...
    LOW(1), NORMAL(2), URGENT(3);

    // TODO(2.2): เพิ่ม private final field และ constructor เก็บ level
    private final int level ;
    Priority(int level) {
        this.level = level ;
    }

    /** ตัวเลขระดับความสำคัญ (มากกว่า = สำคัญกว่า) */
    public int level() {
        // TODO(2.3): คืนค่า level จริง
        return this.level;
    }

    /** true เมื่อระดับของเรา "อย่างน้อยเท่ากับ" ระดับที่กำหนด */
    public boolean isAtLeast(Priority other) {
        // TODO(2.4): เปรียบเทียบ level ของเรากับของ other
        return this.level >= other.level ;
    }
}
