package com.musicapp.streamingservice.util;


public class Range {
    private final long begin;
    private final long end;
    private final boolean partial;
    private final long fileSize;

    private Range(long begin, long end, boolean partial, long fileSize) {
        this.begin = begin;
        this.end = end;
        this.partial = partial;
        this.fileSize = fileSize;
    }

    public long start() { return begin; }
    public long end() { return end; }
    public long length() { return end - begin + 1; }
    public boolean isPartial() { return partial; }
    public long fileSize() { return fileSize; }

    public static Range parse(String header, long totalSize) {
        if (header == null || !header.startsWith("bytes=")) return new Range(0, totalSize - 1, true, totalSize);
        String[] parts = header.substring(6).split("-");
        long start = Long.parseLong(parts[0]);
        boolean partial = true;
        long end = totalSize - 1;
        if (parts.length > 1 && !parts[1].isEmpty()) {
            end = Long.parseLong(parts[1]);
            partial = false;
        }
        return new Range(start, end, partial, totalSize);
    }
}
