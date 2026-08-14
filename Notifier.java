/**
 * Notifier — สัญญากลางของ "ช่องทางแจ้งเตือน" (ให้มาแล้ว ห้ามแก้)
 *
 * ISP: interface เล็กเฉพาะทาง มี method เดียวที่ทุกช่องทางทำได้จริง
 * DIP: NotificationService จะพึ่งพา interface นี้ ไม่พึ่ง concrete class
 * OCP: เพิ่มช่องทางใหม่ = สร้าง class ใหม่ที่ implements Notifier
 *      โดยไม่ต้องแก้โค้ดเดิมแม้บรรทัดเดียว
 */
public interface Notifier {

    /** ส่งข้อความแจ้งเตือนออกไปทางช่องทางนี้ */
    
    void send(String message);
}
