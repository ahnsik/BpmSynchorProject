package myproject;

public class Note {			// 음표(or chord) 1개의 데이터
		public int	timeStamp;
		public String	chordName;
		public String	technic;
		public String	tab[];			// 
		public String	note[];		// 연주 판단할 음정 (4개까지)
		public String	lyric;
		
		public Note() {			// 초기화 하기 위한 생성자
			timeStamp = 0;
			chordName = "";
			technic = "";
			tab = new String[0]; 
			note = new String[0];
			lyric = "";
		}
	
}
