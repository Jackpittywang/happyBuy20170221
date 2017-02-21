package mobi.dotc.socialnetworks.google;

import com.google.android.gms.plus.model.people.Person;

/**
 * Created by brian.dang on 6/4/2015.
 */
public class GoogleUser {

    public static String getGender(int gender) {
        String result = "";
        switch(gender) {
            case Person.Gender.FEMALE:
                result = "Female";
                break;
            case Person.Gender.MALE:
                result = "Male";
                break;
            case Person.Gender.OTHER:
                result = "N/A";
                break;
            default:
                result = "N/A";
                break;
        }

        return result;
    }

    public static String getRelationshipStatus(int status){
        String result = "";
        switch(status){
            case Person.RelationshipStatus.ENGAGED:
                result = "Engaged";
                break;
            case Person.RelationshipStatus.IN_A_RELATIONSHIP:
                result = "In a Relationship";
                break;
            case Person.RelationshipStatus.IN_CIVIL_UNION:
                result = "In Civil Union";
                break;
            case Person.RelationshipStatus.IN_DOMESTIC_PARTNERSHIP:
                result = "In Domestic Partnership";
                break;
            case Person.RelationshipStatus.ITS_COMPLICATED:
                result = "Its Complicated";
                break;
            case Person.RelationshipStatus.MARRIED:
                result = "Married";
                break;
            case Person.RelationshipStatus.OPEN_RELATIONSHIP:
                result = "Open Relationship";
                break;
            case Person.RelationshipStatus.SINGLE:
                result = "Single";
                break;
            case Person.RelationshipStatus.WIDOWED:
                result = "Widowed";
                break;
            default:
                result = "N/A";
                break;
        }
        return result;
    }
}
