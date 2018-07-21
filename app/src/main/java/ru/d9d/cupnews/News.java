package ru.d9d.cupnews;

public class News {

    private long mDateMilliseconds;
    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mUrl;

    /**
     * @param date    - News publication date/time in milliseconds (unix time)
     * @param title   - News title, string
     * @param author  - News author(s) name
     * @param section - News type, ie article, liveblog, quiz
     * @param url     - News link URL
     */
    public News(long date, String title, String author, String section, String url) {
        mDateMilliseconds = date;
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mUrl = url;
    }

    /**
     * @return publication date/time in milliseconds
     */
    public long getDateMilliseconds() {
        return mDateMilliseconds;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isValid() {
        return mTitle != null && mUrl != null;
    }
}
