import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test runner สำหรับ Lab — Song & Notification System
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    /** helper กลาง — พิมพ์ PASS/FAIL และนับผลให้เอง */
    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    /** helper สร้างรายการแท็กแบบ mutable (เหมือน list จริงที่ผู้ใช้ส่งมา) */
    private static List<String> tagsOf(String... ts) {
        return new ArrayList<String>(Arrays.asList(ts));
    }

    /** Notifier ปลอมสำหรับทดสอบ — จดทุกข้อความที่ถูกส่งเข้ามา
     *  นี่คือผลพลอยได้ของ DIP: service พึ่ง interface
     *  เราจึงเสียบตัวปลอมแทนอีเมล/SMS จริงได้ทันที */
    private static class FakeNotifier implements Notifier {
        final List<String> received = new ArrayList<String>();

        public void send(String message) {
            received.add(message);
        }
    }

    public static void main(String[] args) {
        boolean assertsOn = false;
        assert assertsOn = true;
        if (!assertsOn) {
            System.out.println("WARNING: assertions disabled"
                    + " - re-run with: java -ea Week06LabTest\n");
        }

        System.out.println("=== Week 6 Lab Test Suite ===\n");

        testSongCreators();
        testSongImmutability();
        testSongExposure();
        testSongEquality();
        testPriorityEnum();
        testCountingNotifier();
        testNotificationService();

        System.out.println("\n=== Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total : " + (passed + failed));
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");


        if (failed > 0) {
            System.exit(1);
        }
    }

    // --- Partition: input ปกติ / input ที่ผิดเงื่อนไข ---
    private static void testSongCreators() {
        System.out.println("-- Song: Creators --");

        Song s = new Song("Shape of You", "Ed Sheeran",
                tagsOf("pop", "2017"));
        check("new Song -> title stored", s.title().equals("Shape of You"));
        check("new Song -> artist stored", s.artist().equals("Ed Sheeran"));
        check("new Song -> tags preserved in order",
                s.tags().equals(Arrays.asList("pop", "2017")));

        // boundary: ไม่มีแท็กเลยก็เป็นเพลงที่ถูกต้อง
        Song noTags = new Song("Lo-fi Beat", "Unknown", new ArrayList<String>());
        check("new Song(no tags) -> empty tag list", noTags.tags().isEmpty());

        // input ที่ผิดเงื่อนไขต้องโยน exception ไม่ใช่ปล่อยผ่าน
        boolean threwNullTitle = false;
        try {
            new Song(null, "Artist", new ArrayList<String>());
        } catch (IllegalArgumentException e) {
            threwNullTitle = true;
        }
        check("new(null title) -> throws IllegalArgumentException", threwNullTitle);

        boolean threwEmptyTitle = false;
        try {
            new Song("", "Artist", new ArrayList<String>());
        } catch (IllegalArgumentException e) {
            threwEmptyTitle = true;
        }
        check("new(empty title) -> throws IllegalArgumentException", threwEmptyTitle);

        boolean threwNullArtist = false;
        try {
            new Song("Title", null, new ArrayList<String>());
        } catch (IllegalArgumentException e) {
            threwNullArtist = true;
        }
        check("new(null artist) -> throws IllegalArgumentException", threwNullArtist);

        boolean threwNullList = false;
        try {
            new Song("Title", "Artist", null);
        } catch (IllegalArgumentException e) {
            threwNullList = true;
        }
        check("new(null tag list) -> throws IllegalArgumentException", threwNullList);

        boolean threwNullTag = false;
        try {
            new Song("Title", "Artist", Arrays.asList("pop", null));
        } catch (IllegalArgumentException e) {
            threwNullTag = true;
        }
        check("new(list with null tag) -> throws IllegalArgumentException",
                threwNullTag);
    }

    // --- Producer ต้องคืนตัวใหม่ ไม่แก้ตัวเดิม / observer ห้ามมี side effect ---
    private static void testSongImmutability() {
        System.out.println("\n-- Song: Immutability (producer & observers) --");

        Song original = new Song("Karma", "Taylor Swift",
                tagsOf("pop"));
        Song tagged = original.withTag("2022");

        check("withTag -> returns a new object", tagged != original);
        check("withTag -> new object has the extra tag",
                tagged.tags().equals(Arrays.asList("pop", "2022")));
        check("withTag does not mutate the original",
                original.tags().equals(Arrays.asList("pop")));
        check("withTag keeps title and artist",
                tagged.title().equals("Karma")
                        && tagged.artist().equals("Taylor Swift"));

        // input ที่ผิดเงื่อนไขต้องโยน exception
        boolean threwNull = false;
        try {
            original.withTag(null);
        } catch (IllegalArgumentException e) {
            threwNull = true;
        }
        check("withTag(null) -> throws IllegalArgumentException", threwNull);

        boolean threwEmpty = false;
        try {
            original.withTag("");
        } catch (IllegalArgumentException e) {
            threwEmpty = true;
        }
        check("withTag(empty) -> throws IllegalArgumentException", threwEmpty);

        // observer เรียกกี่ครั้งก็ต้องไม่เปลี่ยนสถานะ
        List<String> before = original.tags();
        original.title();
        original.artist();
        original.tags();
        check("observers have no side effects", original.tags().equals(before));
    }

    // --- ทดสอบว่าไม่เกิด representation exposure ---
    private static void testSongExposure() {
        System.out.println("\n-- Song: Representation Exposure --");

        // ขาออก: แก้ list ที่ได้จาก tags() ต้องไม่กระทบ rep
        Song s = new Song("Ditto", "NewJeans", tagsOf("kpop"));

        List<String> got = s.tags();
        got.clear();
        check("clearing result of tags() does not affect the song",
                s.tags().size() == 1);

        got = s.tags();
        got.add("injected");
        check("adding to result of tags() does not affect the song",
                s.tags().equals(Arrays.asList("kpop")));

        // สองครั้งต้องเป็นคนละ object
        check("tags() returns a fresh list each call", s.tags() != s.tags());

        // ขาเข้า: แก้ list ที่ส่งให้ constructor ต้องไม่กระทบ rep
        List<String> input = new ArrayList<String>(Arrays.asList("rock", "90s"));
        Song p = new Song("Creep", "Radiohead", input);

        input.clear();
        check("clearing constructor argument does not affect the song",
                p.tags().size() == 2);

        input.add("injected");
        check("adding to constructor argument does not affect the song",
                !p.tags().contains("injected"));
    }

    // --- equals/hashCode ต้องทำตามสัญญาของ Java ---
    private static void testSongEquality() {
        System.out.println("\n-- Song: equals & hashCode --");

        Song a = new Song("Butter", "BTS", tagsOf("kpop"));
        Song b = new Song("Butter", "BTS", tagsOf("kpop"));
        Song c = new Song("Butter", "BTS", tagsOf("kpop"));
        Song other = new Song("Dynamite", "BTS", tagsOf("kpop"));

        // == เทียบ reference ส่วน equals เทียบค่า
        check("two equal songs are different objects (a != b)", a != b);
        check("reflexive: a.equals(a)", a.equals(a));
        check("structural: a.equals(b) when all fields match", a.equals(b));
        check("symmetric: a.equals(b) == b.equals(a)",
                a.equals(b) == b.equals(a));
        check("transitive: a=b and b=c -> a=c",
                a.equals(b) && b.equals(c) && a.equals(c));
        check("different title -> not equal", !a.equals(other));
        check("different tags -> not equal",
                !a.equals(new Song("Butter", "BTS", tagsOf("pop"))));
        check("non-null: a.equals(null) -> false", !a.equals(null));
        check("different type -> false (no exception)", !a.equals("Butter"));

        // consistent: เรียกซ้ำต้องได้คำตอบเดิม
        check("consistent: repeated equals gives the same answer",
                a.equals(b) && a.equals(b) && a.equals(b));

        // กติกาคู่หู: equal แล้ว hash ต้องเท่า
        check("equal songs -> equal hashCode", a.hashCode() == b.hashCode());

        // ใช้งานจริงกับ HashSet — ถ้า equals/hashCode ถูก object ต้อง "ไม่หายตัว"
        Set<Song> set = new HashSet<Song>();
        set.add(a);
        check("HashSet.contains finds a structurally equal song",
                set.contains(new Song("Butter", "BTS", tagsOf("kpop"))));
        set.add(b);
        check("HashSet treats equal songs as one element", set.size() == 1);
    }

    // --- enum ต้อง type-safe และมีพฤติกรรมของตัวเอง ---
    private static void testPriorityEnum() {
        System.out.println("\n-- Priority enum --");

        check("LOW.level() == 1", Priority.LOW.level() == 1);
        check("NORMAL.level() == 2", Priority.NORMAL.level() == 2);
        check("URGENT.level() == 3", Priority.URGENT.level() == 3);

        check("URGENT is at least NORMAL",
                Priority.URGENT.isAtLeast(Priority.NORMAL));
        check("NORMAL is at least NORMAL (boundary: equal levels)",
                Priority.NORMAL.isAtLeast(Priority.NORMAL));
        check("LOW is NOT at least NORMAL",
                !Priority.LOW.isAtLeast(Priority.NORMAL));
        check("LOW is at least LOW", Priority.LOW.isAtLeast(Priority.LOW));
    }

    // --- Decorator: ห่อแล้วมอบงาน ไม่สืบทอด ---
    private static void testCountingNotifier() {
        System.out.println("\n-- CountingNotifier (composition + delegation) --");

        FakeNotifier fake = new FakeNotifier();
        CountingNotifier counting = new CountingNotifier(fake);

        check("new wrapper starts at count 0", counting.sendCount() == 0);

        counting.send("hello");
        check("send once -> count 1", counting.sendCount() == 1);
        check("send delegates the message to inner",
                fake.received.equals(Arrays.asList("hello")));

        counting.send("world");
        counting.send("!");
        check("send three times -> count 3", counting.sendCount() == 3);
        check("inner received every message in order",
                fake.received.equals(Arrays.asList("hello", "world", "!")));

        // ห่อ "ช่องทางชนิดไหนก็ได้" — จุดแข็งของ composition
        CountingNotifier wrapEmail = new CountingNotifier(new EmailNotifier());
        wrapEmail.send("test email channel");
        check("can wrap any Notifier (EmailNotifier)",
                wrapEmail.sendCount() == 1);

        boolean threwNull = false;
        try {
            new CountingNotifier(null);
        } catch (IllegalArgumentException e) {
            threwNull = true;
        }
        check("new CountingNotifier(null) -> throws IllegalArgumentException",
                threwNull);
    }

    // --- DIP + OCP + defensive copy ของ service ---
    private static void testNotificationService() {
        System.out.println("\n-- NotificationService (DIP, OCP, aliasing-safe) --");

        FakeNotifier ch1 = new FakeNotifier();
        FakeNotifier ch2 = new FakeNotifier();

        List<Notifier> channels = new ArrayList<Notifier>();
        channels.add(ch1);
        channels.add(ch2);

        NotificationService svc =
                new NotificationService(channels, Priority.NORMAL);

        check("channelCount reports 2", svc.channelCount() == 2);

        // ความสำคัญถึงเกณฑ์ -> ส่งครบทุกช่องทาง
        boolean sent = svc.broadcast("server down!", Priority.URGENT);
        check("URGENT on NORMAL threshold -> returns true", sent);
        check("message reached channel 1",
                ch1.received.equals(Arrays.asList("server down!")));
        check("message reached channel 2",
                ch2.received.equals(Arrays.asList("server down!")));

        // boundary: เท่าเกณฑ์พอดีต้องส่ง
        svc.broadcast("weekly report", Priority.NORMAL);
        check("NORMAL on NORMAL threshold -> also sent (boundary)",
                ch1.received.size() == 2);

        // ต่ำกว่าเกณฑ์ -> ไม่ส่ง และคืน false
        boolean low = svc.broadcast("just a meme", Priority.LOW);
        check("LOW on NORMAL threshold -> returns false", !low);
        check("filtered message is NOT delivered", ch1.received.size() == 2);

        // ใช้ร่วมกับ Song ผ่าน announceNewSong
        Song song = new Song("Golden Hour", "JVKE", tagsOf("pop"));
        svc.announceNewSong(song, Priority.URGENT);
        check("announceNewSong builds a message containing the title",
                ch2.received.get(ch2.received.size() - 1).contains("Golden Hour"));

        // aliasing: แก้ list ต้นทางหลังสร้าง service ต้องไม่กระทบ
        channels.clear();
        check("clearing the injected list does not affect the service",
                svc.channelCount() == 2);
        svc.broadcast("after clear", Priority.URGENT);
        check("service still delivers after the caller mutated its list",
                ch1.received.contains("after clear"));

        // input ที่ผิดเงื่อนไขต้องโยน exception
        boolean threwNullList = false;
        try {
            new NotificationService(null, Priority.LOW);
        } catch (IllegalArgumentException e) {
            threwNullList = true;
        }
        check("new(null channels) -> throws IllegalArgumentException",
                threwNullList);

        boolean threwNullMember = false;
        try {
            new NotificationService(Arrays.asList((Notifier) null),
                    Priority.LOW);
        } catch (IllegalArgumentException e) {
            threwNullMember = true;
        }
        check("new(channels with null) -> throws IllegalArgumentException",
                threwNullMember);

        boolean threwNullThreshold = false;
        try {
            new NotificationService(new ArrayList<Notifier>(), null);
        } catch (IllegalArgumentException e) {
            threwNullThreshold = true;
        }
        check("new(null threshold) -> throws IllegalArgumentException",
                threwNullThreshold);

        boolean threwEmptyMsg = false;
        try {
            svc.broadcast("", Priority.URGENT);
        } catch (IllegalArgumentException e) {
            threwEmptyMsg = true;
        }
        check("broadcast(empty message) -> throws IllegalArgumentException",
                threwEmptyMsg);

        // OCP: เพิ่มช่องทางใหม่ = แค่ implements Notifier เพิ่ม
        // (FakeNotifier ในไฟล์นี้เองก็คือ "ช่องทางใหม่" ที่ service ไม่รู้จักมาก่อน)
        NotificationService fresh = new NotificationService(
                Arrays.<Notifier>asList(new CountingNotifier(ch1)),
                Priority.LOW);
        fresh.broadcast("stacked decorators work too", Priority.LOW);
        check("service works with any Notifier, even wrapped ones (OCP)",
                ch1.received.contains("stacked decorators work too"));
    }
}
