import java.util.ArrayList;
import java.util.List;

/**
 * NotificationService — โมดูลระดับสูงที่กระจายข้อความไปทุกช่องทาง
 *
 * โครงสร้างนี้ทำ DIP ถูกแล้วครึ่งทาง: รับ List<Notifier> ฉีดเข้ามาทาง
 * constructor แทนการ new concrete class เอง — แต่ยังเหลือบั๊กจากบทเรียน
 * Part 1: list ที่รับมาเป็น mutable และถูกเก็บลูกศรตรง ๆ (aliasing!)
 * แถมยังไม่ validate input และไม่สนใจ threshold เลย
 */
public final class NotificationService {

    private final List<Notifier> channels;
    private final Priority threshold;

    /**
     * @param channels  ช่องทางทั้งหมด ห้าม null และห้ามมีสมาชิก null
     * @param threshold ระดับต่ำสุดที่จะยอมส่ง ห้าม null
     * @throws IllegalArgumentException เมื่อ input ผิดเงื่อนไข
     */
    public NotificationService(List<Notifier> channels, Priority threshold) {
        // TODO(4.1): validate — channels ห้าม null/มีสมาชิก null,
        //            threshold ห้าม null → throw IllegalArgumentException
        // TODO(4.2): ✗ เก็บลูกศรตรง ๆ เสี่ยง aliasing → defensive copy!
        if(channels == null||channels.contains(null)) throw new IllegalArgumentException() ;
        if(threshold == null)throw new IllegalArgumentException() ;

        this.channels = new ArrayList<>(channels) ;
        this.threshold = threshold;
    }

    /** จำนวนช่องทางที่ลงทะเบียนไว้ */
    public int channelCount() {
        return channels.size();
    }

    /**
     * กระจายข้อความไปทุกช่องทาง ถ้าความสำคัญถึงเกณฑ์
     *
     * @param message  ข้อความ ห้าม null/ว่าง
     * @param priority ความสำคัญของข้อความนี้ ห้าม null
     * @return true เมื่อส่งจริง, false เมื่อความสำคัญต่ำกว่าเกณฑ์ (ไม่ส่ง)
     * @throws IllegalArgumentException เมื่อ input ผิดเงื่อนไข
     */
    public boolean broadcast(String message, Priority priority) {
        // TODO(4.3): validate message (null/ว่าง) และ priority (null)
        if(message == null||message =="")throw new IllegalArgumentException() ;
        if(priority == null)throw new IllegalArgumentException() ;
        // TODO(4.4): ถ้า priority ต่ำกว่า threshold ให้ "ไม่ส่ง" และคืน false
        //            คำใบ้: ใช้ Priority.isAtLeast(...) ที่คุณเพิ่งเขียน
        if(!priority.isAtLeast(threshold)) return false ;
        for (Notifier n : channels) {
            n.send(message);    // polymorphism — ไม่สน concrete type เลย (OCP)
        }
        return true;
    }

    /** ความสะดวก: ประกาศเพลงใหม่ (แสดงการใช้ Song ร่วมกับ service) */
    public boolean announceNewSong(Song song, Priority priority) {
        if (song == null) {
            throw new IllegalArgumentException("song must not be null");
        }
        return broadcast("New release: " + song.title() + " by " + song.artist(),
                priority);
    }
}
