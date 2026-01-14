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
        // No range header means serve the entire file
        if (header == null || !header.startsWith("bytes=")) {
            return new Range(0, totalSize - 1, false, totalSize);
        }
        
        // Parse range header like "bytes=0-1023" or "bytes=1024-"
        String[] parts = header.substring(6).split("-");
        long start = Long.parseLong(parts[0]);
        long end = totalSize - 1;
        
        // If end is specified in the range, use it
        if (parts.length > 1 && !parts[1].isEmpty()) {
            end = Long.parseLong(parts[1]);
        }
        
        // Ensure end doesn't exceed file size
        if (end >= totalSize) {
            end = totalSize - 1;
        }
        
        // It's a partial content if we have a range header
        return new Range(start, end, true, totalSize);
    }
}
