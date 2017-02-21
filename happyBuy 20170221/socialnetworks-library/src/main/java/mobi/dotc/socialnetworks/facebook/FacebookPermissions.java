package mobi.dotc.socialnetworks.facebook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian.dang on 5/13/2015.
 */
public enum FacebookPermissions
{
    /**
     * Each permission has its own set of requirements and suggested use cases. Some permissions do
     * not require review, but most do. Please see the details for each permission to learn more
     * about how to use it in your app. Remember, all use of these permissions are subject to our
     * Platform Policies and your own privacy policy.
     */

    // user id, name, first name, last name, link, gender, locale, timezone, updated_time, verified
    PUBLIC_PROFILE("public_profile"),

    // Provides access the list of friends that also use your app.
    // These friends can be found on the friends edge on the user object.
    USER_FRIENDS("user_friends"),

    // Provides access to the person's primary email address via the email property on the user object.
    EMAIL("email"),

    /**
     * EXTENDED PROFILE PROPERTIES
     * These permissions are not optional in the login dialog during the login flow, meaning
     * they are non-optional for people when logging into your app. If you want them to be optional,
     * you should structure your app to only request them when absolutely necessary and not during
     * initial login.
     */

    // Provides access to a person's personal description
    // (the 'About Me' section on their Profile) through the bio property on the User object.
    USER_ABOUT_ME("user_about_me"),

    // Provides access to all common books actions published by any app the person has used.
    // This includes books they've read, want to read, rated or quoted.
    USER_ACTIONS_BOOKS("user_actions.books"),

    // Provides access to all common Open Graph fitness actions published by any app
    // the person has used. This includes runs, walks and bikes actions.
    USER_ACTIONS_FITNESS("user_actions.fitness"),

    // Provides access to all common Open Graph music actions published by any app the
    // person has used. This includes songs they've listened to, and playlists they've created.
    USER_ACTIONS_MUSIC("user_actions.music"),

    // Provides access to all common Open Graph news actions published by any app the
    // person has used which publishes these actions.
    // This includes news articles they've read or news articles they've published.
    USER_ACTIONS_NEWS("user_actions.news"),

    // Provides access to all common Open Graph video actions published by any app the person
    // has used which publishes these actions. This includes videos they've watched, videos
    // they've rated and videos they want to watch.
    USER_ACTIONS_VIDEO("user_actions.video"),

    // Provides access to all of the person's custom Open Graph actions in the given app.
    USER_ACTIONS_APP_NAMESPACE("user_actions:{app_namespace}"),

    // !!! DEPRECATED !!!
    // Provides access to a person's list of activities as listed on their Profile. This is a subset
    // of the pages they have liked, where those pages represent particular interests.
    // This information is accessed through the activities connection on the user node.
    // !!! DEPRECATED !!!
    USER_ACTIVITIES("user_activities"), // !!! DEPRECATED !!!

    // Access the date and month of a person's birthday. This may or may not include the person's
    // year of birth, dependent upon their privacy settings and the access token being used to
    // query this field.
    USER_BIRTHDAY("user_birthday"),

    // Provides access to a person's education history through the education field on the User object.
    USER_EDUCATION_HISTORY("user_education_history"),

    // Provides read-only access to the Events a person is hosting or has RSVP'd to.
    USER_EVENTS("user_events"),

    // Provides access to read a person's game activity (scores, achievements) in any game the person has played.
    USER_GAMES_ACTIVITY("user_games_activity"),

    // Enables your app to read the Groups a person is a member of through the groups edge on the User object.
    USER_GROUPS("user_groups"),

    // Provides access to a person's hometown location through the hometown field on the User object.
    USER_HOMETOWN("user_hometown"),

    // !!! DEPRECATED !!!
    // Provides access to the list of interests in a person's Profile.
    // This is a subset of the pages they have liked which represent particular interests
    // !!! DEPRECATED !!!
    USER_INTERESTS("user_interests"), // !!! DEPRECATED !!!

    // Provides access to the list of all Facebook Pages and Open Graph objects that a person has
    // liked. This list is available through the likes edge on the User object
    USER_LIKES("user_likes"),

    // Provides access to a person's current city through the location field on the User object.
    // The current city is set by a person on their Profile.
    USER_LOCATION("user_location"),

    // Enables your app to read the Groups a person is an admin of through the groups
    // edge on the User object.
    USER_MANAGED_GROUPS("user_managed_groups"),

    // Provides access to the photos a person has uploaded or been tagged in. This is available through the photos edge on the User object.
    USER_PHOTOS("user_photos"),

    // Provides access to the posts on a person's Timeline. Includes their own posts,
    // posts they are tagged in, and posts other people make on their Timeline.
    USER_POSTS("user_posts"),

    // Provides access to a person's relationship status, significant other and family members
    // as fields on the User object.
    USER_RELATIONSHIPS("user_relationships"),

    // Provides access to a person's relationship interests as the interested_in field on the User object.
    USER_RELATIONSHIP_DETAILS("user_relationship_details"),

    // Provides access to a person's religious and political affiliations.
    USER_RELIGION_POLITICS("user_religion_politics"),

    // Provides access to a person's statuses. These are posts on Facebook which don't include links, videos or photos.
    USER_STATUS("user_status"),

    // Provides access to the Places a person has been tagged at in photos, videos, statuses and links
    USER_TAGGED_PLACES("user_tagged_places"),

    // Provides access to the videos a person has uploaded or been tagged in.
    USER_VIDEOS("user_videos"),

    // Provides access to the person's personal website URL via the website field on the User object.
    USER_WEBSITE("user_website"),

    // Provides access to a person's work history and list of employers via the work field on the User object.
    USER_WORK_HISTORY("user_work_history"),

    /**
     * EXTENDED Permissions
     * Extended Permissions give access to more sensitive information and give your app the ability
     * to publish and delete data. All extended permissions appear on a separate screen during the
     * login flow so a person can decide if they want to grant them.
     */

    // Provides access to the names of custom lists a person has created to organize their friends.
    // This is useful for rendering an audience selector when someone is publishing stories to
    // Facebook from your app.
    READ_CUSTOM_FRIENDLISTS("read_custom_friendlists"),

    // Provides read-only access to the Insights data for Pages, Apps and web domains the person owns
    READ_INSIGHTS("read_insights"),

    // Provides the ability to read the messages in a person's Facebook Inbox through the inbox
    // edge and the thread node.
    READ_MAILBOX("read_mailbox"),

    // Provides the ability to read from the Page Inboxes of the Pages managed by a person.
    // This permission is often used alongside the manage_pages permission.
    READ_PAGE_MAILBOXES("read_page_mailboxes"),

    // Provides access to read the posts in a person's News Feed, or the posts on their Profile.
    READ_STREAM("read_stream"),

    // Enables your app to read a person's notifications and mark them as read.
    MANAGE_NOTIFICATIONS("manage_notifications"),

    // Enables your app to retrieve Page Access Tokens for the Pages and Apps that the person administrates.
    MANAGE_PAGES("manage_pages"),

    // When you also have the manage_pages permission, gives your app the ability to post, comment
    // and like as any of the Pages managed by a person using your app.
    PUBLISH_PAGES("publish_pages"),

    // Provides access to publish Posts, Open Graph actions, achievements, scores and other
    // activity on behalf of a person using your app.
    PUBLISH_ACTIONS("publish_actions"),

    // Provides the ability to set a person's attendee status on Facebook Events
    // (e.g. attending, maybe, or declined).
    RSVP_EVENT("rsvp_event")
    ;

    private final String text;

    private FacebookPermissions(final String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static List<String> getAllUserPermissionsList(){
        List<String> list = new ArrayList<String>();
        list.add(PUBLIC_PROFILE.toString());
        list.add(USER_FRIENDS.toString());
        list.add(EMAIL.toString());
        list.add(USER_ABOUT_ME.toString());

        // Actions
        list.add(USER_ACTIONS_BOOKS.toString());
        list.add(USER_ACTIONS_FITNESS.toString());
        list.add(USER_ACTIONS_MUSIC.toString());
        list.add(USER_ACTIONS_NEWS.toString());
        list.add(USER_ACTIONS_VIDEO.toString());
        // list.add(USER_ACTIONS_APP_NAMESPACE.toString()); // ?

        // list.add(USER_ACTIVITIES.toString()); // !!! DEPRECATED !!!
        list.add(USER_BIRTHDAY.toString());
        list.add(USER_EDUCATION_HISTORY.toString());
        list.add(USER_EVENTS.toString());
        list.add(USER_GAMES_ACTIVITY.toString());
        list.add(USER_GROUPS.toString());
        list.add(USER_HOMETOWN.toString());
        // list.add(USER_INTERESTS.toString()); // !!! DEPRECATED !!!
        list.add(USER_LIKES.toString());
        list.add(USER_LOCATION.toString());

        list.add(USER_MANAGED_GROUPS.toString());
        list.add(USER_PHOTOS.toString());
        list.add(USER_RELATIONSHIPS.toString());
        list.add(USER_RELATIONSHIP_DETAILS.toString());
        list.add(USER_RELIGION_POLITICS.toString());
        list.add(USER_STATUS.toString());
        list.add(USER_TAGGED_PLACES.toString());
        list.add(USER_VIDEOS.toString());
        list.add(USER_WEBSITE.toString());
        list.add(USER_WORK_HISTORY.toString());

        // Read Permissions
        list.add(READ_CUSTOM_FRIENDLISTS.toString());
        list.add(READ_INSIGHTS.toString());
        list.add(READ_MAILBOX.toString());
        list.add(READ_PAGE_MAILBOXES.toString());
        list.add(READ_STREAM.toString());

        // Manage Permissions
//        list.add(MANAGE_NOTIFICATIONS.toString());
//        list.add(MANAGE_PAGES.toString());
//
//        // Publish Permissions
//        list.add(PUBLISH_PAGES.toString());
//        list.add(PUBLISH_ACTIONS.toString());
//
//        // RSVP
//        list.add(RSVP_EVENT.toString());

        return list;
    }
}
