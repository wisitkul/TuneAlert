/**
 * CountingNotifier — ตัวห่อ (wrapper) ที่นับจำนวนครั้งการส่ง
 *
 * "Prefer composition over inheritance":
 * ห้ามสืบทอด EmailNotifier/SmsNotifier (ไม่ใช่ is-a เชิงพฤติกรรม
 * และจะห่อได้ทีละชนิด) — ให้ "ถือ" Notifier ตัวในไว้ (has-a)
 * แล้ว "มอบงาน" (delegate) ให้มันทำ จึงห่อช่องทางชนิดไหนก็ได้
 *
 * รูปแบบนี้คือ Decorator pattern แบบเดียวกับ CountingSet ในสไลด์
 */
public final class CountingNotifier implements Notifier {

    // TODO(3.1): ประกาศ field เก็บ Notifier ตัวใน (has-a) และตัวนับ
    //            คำใบ้: รู้จักตัวในผ่าน "interface Notifier" เท่านั้น
    private final Notifier inner ;
    private int count ;

    /**
     * @param inner ช่องทางจริงที่จะมอบงานให้ ห้าม null
     * @throws IllegalArgumentException เมื่อ inner เป็น null
     */
    public CountingNotifier(Notifier inner) {
        // TODO(3.2): validate แล้วเก็บ inner
        if(inner == null) throw new IllegalArgumentException() ;
        this.inner = inner ;
        count = 0 ;
    }

    @Override
    public void send(String message) {
        // TODO(3.3): นับหนึ่งครั้ง แล้ว delegate ให้ inner.send(...)
        inner.send(message) ;
        count++ ;
    }

    /** จำนวนครั้งที่ send ถูกเรียกบน wrapper ตัวนี้ */
    public int sendCount() {
        // TODO(3.4): คืนค่าตัวนับจริง
        return this.count;
    }
}
