package hogent.reddit.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Yannick on 19/03/2017.
 */

public class Post implements Serializable, Parcelable {
    String title, author, thumbnail, urlPicture, upvotes, text, subReddit, fullName;

    public Post(String title, String author, String thumbnail, String urlPicture, String upvotes, String text, String subReddit, String fullName) {
        this.title = title;
        this.author = author;
        this.thumbnail = thumbnail;
        this.urlPicture = urlPicture;
        this.upvotes = upvotes;
        this.text = text;
        this.subReddit = subReddit;
        this.fullName = fullName;
    }

    public Post(String title, String author, String thumbnail, String urlPicture, String text, String subReddit, String fullName) {
        this.title = title;
        this.author = author;
        this.thumbnail = thumbnail;
        this.urlPicture = urlPicture;
        this.text = text;
        this.subReddit = subReddit;
        this.fullName = fullName;
        this.upvotes = "";
    }

    public Post(Parcel in) {
        title = in.readString();
        author = in.readString();
        thumbnail = in.readString();
        urlPicture = in.readString();
        upvotes = in.readString();
        text = in.readString();
        subReddit = in.readString();
        fullName = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public String getText() {
        return text;
    }

    public String getSubReddit() {
        return subReddit;
    }

    public String getFullName() {
        return fullName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(thumbnail);
        dest.writeString(urlPicture);
        dest.writeString(upvotes);
        dest.writeString(text);
        dest.writeString(subReddit);
        dest.writeString(fullName);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
