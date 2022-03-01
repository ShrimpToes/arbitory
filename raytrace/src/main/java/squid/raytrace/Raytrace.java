package squid.raytrace;

public class Raytrace {

    static {
        System.loadLibrary("raytrace");
    }

    private static native long create_sb(int nx, int ny);
    private static native int[] render_image(long sb, int nx, int ny, int sample_size);
    private static native void free_cuda(long sb);

    private final long sb;
    private final int width;
    private final int height;
    private int sample_size;

    public Raytrace(int width, int height, int sample_size) {
        this.width = width;
        this.height = height;
        sb = create_sb(width, height);
        this.sample_size = sample_size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] raytrace() {
        return render_image(sb, width, height, sample_size);
    }

    public void free() {
        free_cuda(sb);
    }

}
