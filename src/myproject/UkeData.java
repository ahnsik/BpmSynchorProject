package myproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class UkeData {
	public String version;				// Uke 파일 형식(버전)을 확인해야 함 
	public String mMusicUrl;			// 음악파일 URL (Only *.WAV file is available for NOW )
	public String mThumbnailUrl;	// 곡 앨범의 대표 사진 
	public String mSongTitle;			// 곡 제목
	public String mCategory;			// 카테고리 (연습분야별)
	public String mAuthor;				// UKE 문서 파일만든 사람.
	public String mCommentary;		// 곡에 대한 간단한 설명 
	public String mBasicMeter;		// 박자 : 2/4, 3/4, 4/4, 6/8 etc....
	public int mStartOffset;			// WAV 파일 기준으로 악보가 시작되는 위치 offset (milli-sec)
//	public int mLevel;					// 난이도 수준
	public float mBpm;					// BPM - 분당 beat 수. (=1분당 4분음표 갯수)
	public int numNotes;				// 전체 음표(chord) 갯수
	public Note[] notes;

	class Note {			// 음표(or chord) 1개의 데이터
		public int	timeStamp;
		public String	chordName;
		public String	technic;
		public String	tab[];			// 
		public String	note[];		// 연주 판단할 음정 (4개까지)
		public String	lyric;
		
		public void	Note() {			// 초기화 하기 위한 생성자
			timeStamp = 0;
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
		mThumbnailUrl = null; 
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
			mThumbnailUrl = (String)jsonObj.getString("thumbnail"); 
			mSongTitle = (String)jsonObj.getString("title");
			try {
				mBasicMeter = (String)jsonObj.getString("basic_beat");		// 박자 : 2/4, 3/4, 4/4, etc..
			} catch (JSONException jsonE) {
				mBasicMeter = "4/4";			// 없으면 default 는 4/4 박자.
			}
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
				mCommentary = (String)jsonObj.getString("comment");		// "comments" 아님.  주의할 것.
			} catch (JSONException jsonE) {
				mCommentary = "undefined";
			}
			// 음표 (연주데이터) 읽기.
			JSONArray jsonNotes = (JSONArray)jsonObj.getJSONArray("notes");
			numNotes = jsonNotes.length();
			
			if ( (jsonNotes != null) && (numNotes > 0) ) {
				numNotes = jsonNotes.length();
				notes = new Note[numNotes];
				for (int i=0; i<numNotes; i++) {
					JSONObject one = jsonNotes.getJSONObject(i);
					notes[i] = new Note();
					notes[i].timeStamp = one.getInt("timestamp");
					notes[i].chordName = one.getString("chord");
//					System.out.println(" notes["+i+"].chordName=" + notes[i].chordName );
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
						notes[i].tab[j] = (String)tabArray.getString(j);
					}
					JSONArray noteArray = (JSONArray)one.getJSONArray("note");
					notes[i].note = new String[noteArray.length()];
					for (j = noteArray .length()-1; j>= 0; j--) {
						notes[i].note[j] = (String)noteArray.getString(j);
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

	/**
	 * uke 데이터에 새로운 음표노드를 추가하는 함수. (빈 note 하나를 추가함) - 이후에 가사/테크닉/TAB음표 등을 설정해야 함. 
	 * @param msec	추가할 위치 (milli-second단위)
	 * @return		note가 추가된 위치 (index) 
	 */
	public int appendNote(int msec) {
		int i, j;
		Note tempArray[] = new Note[notes.length+1];	// 갯수 하나 늘려서 배열 만들고,
		System.out.println("Apped new at:" + msec );
		for (i=0; i<notes.length; i++) {
//			System.out.println("copying.."+i + "(ts:" + notes[i].timeStamp+")" );
			if (notes[i].timeStamp == msec) {
				// 중복되는 값이 존재하므로 취소 하고,  index 는 -1 을 리턴.
				return -1;
			} else if (notes[i].timeStamp > msec) {	// timeStamp 값이 msec 보다 커지는 시점에서 break;  i 값이 새로 추가되는 index 값.
				break;
			} else {		// timeStamp 값이 msec 보다 작은 것은 배열복사.
				tempArray[i] = notes[i];
			}
		}
		tempArray[i] = new Note();
		tempArray[i].timeStamp = msec;		// 새로운 note 로 추가된 것.
		for (j=i; j<notes.length; j++) {	// 배열 나머지 복사. 
			tempArray[j+1] = notes[j];
		}

		// 배열 복사가 끝났으면, notes 배열로 바꿔치기 해 줌.
		notes = tempArray;
//		System.arraycopy(notes, 0, tempArray, 0, notes.length );	// 일단 왕창 복사해 넣고 나서,
		return i;		// 새로 추가된 위치(index)를 리턴해 줌.
	}

	/**
	 * 시간 값을 기준으로, 해당 시간의 note 데이터를 제거 한다.
	 * @param msec		제거할 note 음의 위치 (msec)
	 */
	public void removeNote(int msec) {
		int i, j;
		Note tempArray[] = new Note[notes.length-1];
		System.out.println("Remove from:" + msec+" msec" );
		for (i=0; i<notes.length; i++) {
			if (notes[i].timeStamp == msec) {
				break;	// 중복되는 값이 존재하면 건너 뜀 ==> TODO: msec 포함하는 grid 를 판단하도록 수정 필요.
			} else {		// timeStamp 값이 msec 보다 작은 것은 배열복사.
				tempArray[i] = notes[i];
			}
		}
		// 배열 나머지 복사
		for (j=i; j<notes.length-1; j++) {
			tempArray[j] = notes[j+1];
		}
		// 배열 복사가 끝났으면, notes 배열로 바꿔치기 해 줌.
		notes = tempArray;
	}

	/**
	 *  배열 인덱스 값으로 note 음표 노드 하나를 제거함.
	 * @param index		제거할 node 의 index 위치.
	 */
	public void removeNoteAt(int index) {
		int i, j;
		Note tempArray[] = new Note[notes.length];
		for (i=0; i<notes.length; i++) {
			if ( i == index) {
				break;	// 중복되는 값이 존재하면 건너 뜀 ==> TODO: msec 포함하는 grid 를 판단하도록 수정 필요.
			} else {		// timeStamp 값이 msec 보다 작은 것은 배열복사.
				tempArray[i] = notes[i];
			}
		}
		// 배열 나머지 복사
		for (j=i; j<notes.length-1; j++) {
			tempArray[j] = notes[j+1];
		}
		// 배열 복사가 끝났으면, notes 배열로 바꿔치기 해 줌.
		notes = tempArray;
	}

	/**
	 * 편집된 데이터를 Uke 파일로 저장하는 함수.
	 * @param f		*.uke 파일 핸들러
	 * @return		성공여부
	 */
	public boolean SaveToFile(File f) {
		if (f==null) {
			System.out.println("File Open Error. !!!");
			return false;
		}

		JSONObject writeObj = new JSONObject();
		try {
			writeObj.put("version", version);
			writeObj.put("source", mMusicUrl);
			writeObj.put("thumbnail", mThumbnailUrl);
			writeObj.put("title", mSongTitle);
			writeObj.put("category", mCategory);
			writeObj.put("author", mAuthor);
			writeObj.put("comment", mCommentary);
			writeObj.put("basic_beat", mBasicMeter);
			writeObj.put("start_offset", mStartOffset);
//			writeObj.put("level", mLevel);
			writeObj.put("bpm", mBpm);
			JSONArray notesArray = new JSONArray();
			for (int i=0; i<notes.length; i++) {
				JSONObject noteData = new JSONObject();
				noteData.put("lyric", notes[i].lyric);
				noteData.put("technic", notes[i].technic);
				JSONArray soundArray = new JSONArray();
				if (notes[i].note == null) {
					noteData.put("note", soundArray);
				} else {
					for (int j=0; j<notes[i].note.length; j++) {
						soundArray.put(notes[i].note[j]);
					}
					noteData.put("note", soundArray);
				}
				JSONArray tabArray = new JSONArray();
				if (notes[i].tab == null) {
					noteData.put("tab", tabArray);
				} else {
					for (int j=0; j<notes[i].tab.length; j++) {
						tabArray.put(notes[i].tab[j]);
					}
					noteData.put("tab", tabArray);
				}
				if (notes[i].chordName == null) {
					noteData.put("chord", "" );
				} else {
					noteData.put("chord", notes[i].chordName);
				}
				noteData.put("timestamp", notes[i].timeStamp);
				notesArray.put(noteData);
			}
			writeObj.put("notes", notesArray);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//			System.out.println("File Open Error. !!!");
			writer.write( writeObj.toString() );
			System.out.println("JSON:"+ writeObj.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

    public String getComment() {
    	return this.mCommentary;
    }
    public String getSongTitle() {
    	return this.mSongTitle;
    }

	public int getSize() {
		// TODO Auto-generated method stub
		return numNotes;
	}

}
