package myproject;

import java.io.File;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class UkeData {
	public String version;				// Uke 파일 형식(버전)을 확인해야 함 
	public String mMusicUrl;			// 음악파일 URL (Only *.WAV file is available for NOW )
	public String mThembnailUrl;	// 곡 앨범의 대표 사진 
	public String mSongTitle;			// 곡 제목
	public String mCategory;			// 카테고리 (연습분야별)
	public String mAuthor;				// UKE 문서 파일만든 사람.
	public String mCommentary;		// 곡에 대한 간단한 설명 
	public String mBasicMeter;		// 박자 : 2/4, 3/4, 4/4, 6/8 etc....
	public int mStartOffset;			// WAV 파일 기준으로 악보가 시작되는 위치 offset (milli-sec)
//	public int mLevel;					// 난이도 수준
	public float mBpm;					// BPM - 분당 beat 수. (=1분당 4분음표 갯수)
	public int numNotes;				// 전체 음표(chord) 갯수
	private Note[] notes;

	class Note {			// 음표(or chord) 1개의 데이터
		public long	timeStamp;
		public String	chordName;
		public String	technic;
		public String	tab[];			// 
		public String	note[];		// 연주 판단할 음정 (4개까지)
		public String	lyric;
		
		public void	Note() {			// 초기화 하기 위한 생성자
			timeStamp = 0L;
			chordName = "";
			technic = "";
			tab = new String[0]; 
			note = new String[0];
			lyric = "";
		}
	}

	public UkeData() {				// 생성자.
		version = null;
		mMusicUrl = null;
		mThembnailUrl = null; 
		mSongTitle = null;
		mCategory = null;
		mAuthor = null;
		mCommentary = null; 
		mBasicMeter = null;
		mStartOffset = 0;
//		mLevel = 0;
		mBpm = 60.0f;
		numNotes = 0;
		notes = null;		// 갯수가 0개.
	}
	
	/**
	 * Uke 파일로 부터 JSON 데이터를 읽어 와서 notes 데이터를 모두 읽어 들임. 
	 * @param f		*.uke 파일 핸들러
	 * @return		성공여부
	 */
	public boolean loadFromFile(File f) {
		JSONParser	parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(f));
			System.out.println("JSON Object: " + obj);
			
			JSONObject jsonObj = new JSONObject(obj.toString());
			// 필수 항목 체크
			mMusicUrl = (String)jsonObj.getString("source");
			mThembnailUrl = (String)jsonObj.getString("thumbnail"); 
			mSongTitle = (String)jsonObj.getString("title");
			mBasicMeter = (String)jsonObj.getString("basic_beat");		// 박자 : 2/4, 3/4, 4/4, etc..
			mStartOffset = (int)jsonObj.getInt("start_offset");
			mBpm = (float)jsonObj.getDouble("bpm");
			// 옵션 항목 체크
			try {
				version = (String)jsonObj.getString("version");		// 옛날 껀 없을 수 도 있음. 
			} catch (JSONException jsonE) {
				version = "undefined";
			}
			try {
				mCategory = (String)jsonObj.getString("category");
			} catch (JSONException jsonE) {
				version = "undefined";
			}
			try {
				mAuthor = (String)jsonObj.getString("author");
			} catch (JSONException jsonE) {
				mAuthor = "undefined";
			}
			try {
				mCommentary = (String)jsonObj.getString("comments");
			} catch (JSONException jsonE) {
				mCommentary = "undefined";
			}
			// 음표 (연주데이터) 읽기.
			JSONArray jsonNotes = (JSONArray)jsonObj.getJSONArray("comments");
			numNotes = jsonNotes.length();
			
			if ( (jsonNotes != null) && (numNotes > 0) ) {
				numNotes = jsonNotes.length();
				notes = new Note[numNotes];
				for (int i=0; i<numNotes; i++) {
					JSONObject one = jsonNotes.getJSONObject(i);
					notes[i] = new Note();
					notes[i].timeStamp = one.getInt("timeStamp");
					notes[i].chordName = one.getString("chord");
					try {
						notes[i].technic = one.getString("technic");
					} catch (JSONException jsonE) {
						notes[i].technic = "";
					}
					try {
						notes[i].lyric = one.getString("lyric");
					} catch (JSONException jsonE) {
						notes[i].lyric = "";
					}
					int j;
					JSONArray tabArray = (JSONArray)one.getJSONArray("tab");
					notes[i].tab = new String[tabArray.length()];
					for (j = tabArray.length()-1; j>=0; j--) {
						notes[i].tab[j] = (String)tabArray.getString(i);
					}
					JSONArray noteArray = (JSONArray)one.getJSONArray("note");
					notes[i].note = new String[noteArray.length()];
					for (j = noteArray .length()-1; j>= 0; j--) {
						notes[i].note[j] = (String)noteArray.getString(i);
					}
				}
			} else {
				System.out.println("JSONArray parsing Error. or no Note Data. (numNotes="+numNotes+"), jsonNotes="+jsonNotes);
				System.out.println("  so, making jsonNotes NULL.");
				numNotes = 0;
				jsonNotes = null;
			}
			return true;
		} catch (Exception e) {
			System.out.println("JSON format has some problem.");
			e.printStackTrace();
		}
		return false;
	}
	

//	public String getVersion() {
//		return version;
//	}
//	public void setVersion(String versionString) {
//		version = versionString;
//	}
//	public String mMusicUrl;			// 음악파일 URL (Only *.WAV file is available for NOW )
//	public String mThembnailUrl;	// 곡 앨범의 대표 사진 
//	public String mSongTitle;			// 곡 제목
//	public String mCategory;			// 카테고리 (연습분야별)
//	public String mAuthor;				// UKE 문서 파일만든 사람.
//	public String mCommentary;		// 곡에 대한 간단한 설명 
//	public String mBasicMeter;		// 박자 : 2/4, 3/4, 4/4, 6/8 etc....
//	public int mStartOffset;			// WAV 파일 기준으로 악보가 시작되는 위치 offset (milli-sec)
////	public int mLevel;					// 난이도 수준
//	public float mBpm;					// BPM - 분당 beat 수. (=1분당 4분음표 갯수)
//	public int numNotes;				// 전체 음표(chord) 갯수
//	private Note[] notes;

	/**
	 * Uke 파일 (또는 편집 중인 데이터의) 음 갯수를 리턴함. - 주로 배열 확보를 위해서 사용됨
	 * @return numNotes
	 */
	public int getSize() {
		return numNotes;
	}

	/**
	 * 편집하던 Notes 데이터 들을 *.uke 파일로 저장하는 함수.
	 * @param f  저장할 파일 핸들러
	 * @return  성공여부
	 */
	public boolean saveToFile(File f) {
		// 현재는 구현되어 있지 않음. 
		return false;
	}
}
