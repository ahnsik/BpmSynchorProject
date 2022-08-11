package myproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class NoteData {

    public  String  version;          // NoteData 援ъ“�쓽 踰꾩쟾 - stroke �굹  hammering-on �벑�쓽 湲곕쾿�룄 �몴�떆�븷 �닔 �엳�룄濡� ��鍮꾪븳 援ъ“泥�

    public  String  mMusicURL;          // �뿰二쇳븷 �쓬�븙 MP3 二쇱냼 �삉�뒗 YouTube 二쇱냼..
    public  String  mThumbnailURL;          // �뿰二쇳븷 �쓬�븙 MP3 二쇱냼 �삉�뒗 YouTube 二쇱냼..
    public  String  mSongTitle;         // 怨≪쓽 �젣紐�
    public  String  mCategory;          // �뿰二쇰갑踰� : Chord / 諛대뱶(�떒�쓬)�뿰二� / �븨嫄곗뒪���씪 / �븘瑜댄럹吏��삤, etc..
    public  String  mAuthor;            // �븙蹂� �젣�옉�옄
    public  String  mCommentary;        // 洹� �쇅�뿉 �씠�윭 ���윭�븳 肄붾찘�듃.
    public  String  mBasicBeat;         // 諛뺤옄 : 2/4, 3/4, 4/4, 6/8, ...

    public  int     mStartOffset;       // 泥섏쓬 �떆�옉�븷 �쐞移섏쓽 �삤�봽�뀑
    public  int     mLevel;             // 怨≪쓽 �궃�씠�룄 �젅踰�. �닽�옄媛� �쟻�쓣 �닔濡� �돩�슫 �젅踰�.
    public  float   mBpm;
    public  int     numNotes;

    // �뿬湲� �븘�옒�쓽 諛곗뿴�뱾�� 吏꾩쭨 �뿰二쇳빐�빞 �븷 �뜲�씠�꽣 �뱾..
    public  long[]       timeStamp;
    public  String[]    chordName;
    public  String[]    stroke;
    public  String[]    technic;
    public  String[][]  tab;
    public  String[][]  note;
    public  boolean[][] note_played;
    public  String[]    lyric;

    public  int[]  score;        // time diff what with played.


    public  NoteData() {
        mMusicURL = null;
        mThumbnailURL = null;
        mSongTitle = null;
        mStartOffset = 0;
        mBpm = 0.0f;
        numNotes = 0;
        // �뿬湲� �븘�옒�쓽 諛곗뿴�뱾�� 吏꾩쭨 �뿰二쇳빐�빞 �븷 �뜲�씠�꽣 �뱾..
        timeStamp = null;
        chordName = null;
        stroke = null;
        technic = null;
        tab = null;
        note = null;
        note_played = null;
        score = null;
    }

//    public  NoteData(String dataFileString) {
//
//        setData(dataFileString);
//    }

    public boolean loadFromFile(File dir, String fileName) {
        String   UkeDataRead;
        System.out.println("loadFromFile : "+dir+"/"+fileName );
        UkeDataRead = readTextFile(dir+"/"+fileName );
        return setData(UkeDataRead);
    }

    private String readTextFile(String path) {
        String  datafile = null;
        File file = new File(path);
        String  line;
        try {
            FileReader fr = new FileReader(file);
            if (fr==null) {
                System.out.println("File Reader Error:" + fr);
                return null;
            }
            BufferedReader buffrd = new BufferedReader(fr);
            if (buffrd==null) {
                System.out.println("File Buffered Read Error:" + buffrd);
                return null;
            }
            datafile = "";
            System.out.println("Readey to vote !!");
            while ( (line=buffrd.readLine() ) != null) {
                if (line == null || line.trim().length() <= 0) {
                    System.out.println("Skip Empty line. !!");
                } else if ( (line.charAt(0)=='#') && (line.charAt(1)=='#') ) {     // 泥섏쓬 �떆�옉�븯�뒗寃� ##濡� �떆�옉�븯�뒗 �씪�씤�� comment 濡� 泥섎━ �븿.
                    System.out.println("This Line is comments. !!" );
                } else {
                    datafile += line;
                }
            }
            System.out.println("buffrd.close !!");
            buffrd.close();
            fr.close();
//            System.out.println("fullText="+datafile);
        } catch(Exception e) {
            System.out.println("Exceptions ");
            e.printStackTrace();
        }
        return datafile;
    }



    public JSONObject makeJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("source", mMusicURL );
            json.put("thumbnail", mThumbnailURL );
            json.put("title", mSongTitle);
            json.put("category", mCategory);
            json.put("author", mAuthor );
//            json.put("author_note", mAuthorNote);
//            json.put("author_comment", mAuthorComment);
//            json.put("create_date", mDateCreated );
            json.put("comment", mCommentary);
            json.put("basic_beat", mBasicBeat);
            json.put("start_offset", mStartOffset);
            json.put("bpm", mBpm);

            JSONArray notes = new JSONArray();
            for (int i=0; i< numNotes; i++) {
                JSONObject oneChord = new JSONObject();
                oneChord.put("timestamp", timeStamp[i] );
                if ( (chordName[i] !=null) && ( ! chordName[i].isEmpty() ) ) {
                    oneChord.put("chord", chordName[i] );
                }
                if ( (stroke[i] !=null) && ( ! stroke[i].isEmpty() ) ) {
                    oneChord.put("stroke", stroke[i] );
                }
                if ( (technic[i] !=null) && ( ! technic[i].isEmpty() ) ) {
                    oneChord.put("technic", technic[i] );
                }

                JSONArray tabJ= new JSONArray();
                for (int j=0; j<tab[i].length; j++) {
                    tabJ.put(tab[i][j]);
                }
                oneChord.put("tab", tabJ);
                JSONArray noteJ= new JSONArray();
                for (int j=0; j<note[i].length; j++) {
                    noteJ.put(note[i][j]);
                }
                oneChord.put("note", noteJ);
                oneChord.put("lyric", lyric[i]);

                notes.put(oneChord);
            }
            json.put("notes", notes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    public boolean  setData(String dataFileString) {

//        System.out.println("-=========== DataFile Dump ===========-");
//        System.out.println(dataFileString );
//        System.out.println("-=========== DataFile Dump END ===========-");
        try {
            System.out.println("start parse" );
            JSONObject  ukeData = new JSONObject(dataFileString);

            this.mMusicURL = ukeData.getString("source");
            System.out.println(" ** very important: source - " + this.mMusicURL );
            this.mThumbnailURL = ukeData.getString("thumbnail");
            this.mSongTitle = ukeData.getString("title");

            this.mStartOffset = ukeData.getInt("start_offset");
            this.mBpm = (float) ukeData.getDouble("bpm");

            JSONArray noteData = ukeData.getJSONArray("notes" );
            this.numNotes = noteData.length();
//            this.playtime = ukeData.getLong("playtime");

            System.out.println("Title: " + this.mSongTitle + ", BPM: "+ this.mBpm );
            System.out.println("notes.length= " + this.numNotes );

            this.timeStamp = new long[this.numNotes];
            this.score = new int[this.numNotes];
            this.chordName = new String[this.numNotes];
            this.stroke = new String[this.numNotes];
            this.technic = new String[this.numNotes];
            this.tab = new String[this.numNotes][];
            this.note = new String[this.numNotes][];
            this.note_played = new boolean[this.numNotes][];
            this.lyric = new String[this.numNotes];

            for (int i = 0; i<this.numNotes; i++) {
                JSONObject  a_note = noteData.getJSONObject(i);
                this.score[i] = 99999999;
                this.timeStamp[i] = a_note.getLong("timestamp");
                try {
                    this.chordName[i] = a_note.getString("chord");
                } catch (Exception e) {
                    this.chordName[i] = null;
                }
                try {
                    this.stroke[i] = a_note.getString("stroke");
                } catch (Exception e) {
                    this.stroke[i] = null;
                }
                try {
                    this.technic[i] = a_note.getString("technic");
                } catch (Exception e) {
                    this.technic[i] = null;
                }
                JSONArray   temp1 = a_note.getJSONArray("tab");
                this.tab[i] = new String[temp1.length()];
                for (int j=0; j<temp1.length(); j++) {
                    this.tab[i][j] = temp1.getString(j);
                }
                JSONArray   temp2 = a_note.getJSONArray("note");
                this.note[i] = new String[temp2.length()];
                this.note_played[i] = new boolean[temp2.length()];
                for (int j=0; j<temp2.length(); j++) {
                    this.note[i][j] = temp2.getString(j);
                    this.note_played[i][j] = false;
                }
                try {
                    this.lyric[i] = a_note.getString("lyric");
//                    System.out.println("lyric : " + lyric[i] );
                } catch (Exception e) {
                    this.lyric[i] = null;
                }
            }

            try {
                System.out.println("start to parsing.." );
                this.mBasicBeat = ukeData.getString("basic_beat");
                System.out.println("reading mBasicBeat : " +  this.mBasicBeat );
                this.mCommentary = ukeData.getString("comment");
                System.out.println("reading mCommentary : " +  this.mCommentary );
//                this.mCategory = ukeData.getString("category");
//                this.mAuthor = ukeData.getString("author");
                System.out.println("mCommentary :"+this.mCommentary );
//                this.mAuthorNote = ukeData.getString("auther_note");
//                this.mAuthorComment = ukeData.getString("auther_comment");
//                this.mDateCreated = ukeData.getString("create_date");
            } catch (Exception e) {
                System.out.println("[][][][][][] Parsing Error for sub-informations [][][][][][] ");
                System.out.println("mCategory :"+this.mCategory );
                System.out.println("mAuthor :"+this.mAuthor );
//                System.out.println("mAuthorNote :"+this.mAuthorNote );
//                System.out.println("mAuthorComment :"+this.mAuthorComment );
//                System.out.println("mDateCreated :"+this.mDateCreated );
                System.out.println("mCommentary :"+this.mCommentary );
            }

        } catch (Exception e) {
            System.out.println("-xxxxxxxxxxxx Error to parse JSON xxxxxxxxxxxx-");
            e.printStackTrace();
            return false;
        }
        return true;
    }   // end of setData();

}
