# BpmSynchorProject (개발 중단 -->  Web기반 App 으로 변경됨)
# -->  ahnsik/uke_blog 레포지토리에,  "new_edit" 브랜치로 구현했음.

---
 Tools for UkeTutor to make synchronized *uke with mp3.<br>
 우쿨렐레 연습기 프로그램 uketutor의 파일 형식 *.uke 파일에 MP3에 timestamp 를 동기화 하는 도구.<br>

 ### Hwo to Use 사용방법 (구상 중)
 ---
 1. 새파일(New File)
 2. Set MP3 버튼으로 MP3 파일을 열기.
 3. Set Album Image 버튼으로 대표 이미지 열기.
 4. 제목 입력
 5. 기본음표 (quaver: 8분음표, 또는 semi-quaver: 16분음표) 를 선택. 
    - GRID 의 1칸에 해당하는 음표의 길이.
 6. 박자 (beat) 를 선택. 
    - 한 마디 안에 박자를 선택. 2/4, 3/4, 4/4, 6/8, ...
 7. BPM 을 선택 및 조정
    - WAV file 의 파형을 앞뒤로 이동해 가며 비교하여, Spinner를 이용하여 BPM 값을 설정.
    - PLAY 버튼을 눌러서 MP3 파일을 재생하여 BPM 값을 조정.
 8. Time Bar 의 미세조정(tune)버튼을 이용하여 각각 node 의 timing 을 미세하게 조정. 
 9. 저장 및 발행. 
 
 ### 개발에 사용한 언어 및 개발 툴
 ---
 JAVA Swing, Eclipse
