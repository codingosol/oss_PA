// https://github.com/psw9808/Java-Term-Project 에서 추가 기능 + GUI를 이용해 시각화하기로 함
// 시대 배경 조선시대로 바꾸고 이벤트 추가함
// 점수 시스템 추가
// 스코어보드 시스템 추가
// GUI 적용 시작
// 스코어보드에 남을 이름을 입력하는 LoginScreen 창 추가
// 스토리를 띄워줄 StoryScreen 추가, 여러 번 플레이 할때 매번 뜨지 않도록 Story 위치 조절
// 게임 플레이가 이루어지는 GameScreen 추가, start 내부에 GameScreen 추가
// +) 기존의 states에서 이루어졌던 sysout 함수들을 모두 textarea에 관련된 append함수로 수정
// 게임 종료 창과 스코어보드를 출력하는 EndScreen 추가
// +) 기타 함수 parameter와 코드 수정
// java GUI는 도커에서 작동 X, 그냥 일반 window에서만 실행하기로 함


import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class Kingdom{ // 메인 클래스 Kingdom 
   public static void main(String[]args){
      
      Systems systems = new Systems();
      Statesbase statesbase = new Statesbase();
      States states = new States();
      Events events = new Events();

      StoryScreen story = new StoryScreen(); // 최초 1회 Story 창 띄움

      while(true){ // 게임 종료 전까지 반복
         states.year=1;
         systems.start(states,events); // 게임 시작
         if(systems.end(states)==true){ // 종료 여부 확인
            break;
         }
      }
   }
}

class LoginScreen extends JFrame{ // 스코어보드에 저장될 사용자 이름을 입력하는 GUI
	JTextField Username;
	String username;
	LoginScreen(){
		setTitle("Enter your username");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(2,1));
		setLocation(700,450);
      setVisible(true);
      // 기초 GUI 설정
		JPanel panel = new JPanel();
		JLabel username = new JLabel("Username : ");
		JButton Button = new JButton("Enter");
		username.setPreferredSize(new Dimension(80,10));
		Button.addActionListener(new ButtonClickListener());
		Username = new JTextField(20);
		panel.add(username);
		panel.add(Username);
		Container c = getContentPane();
		c.add(panel);
		c.add(Button);
		pack();
		// GUI 창의 세부 설정 및 실행
		// 사용자 이름 입력 전까지 대기
        synchronized(this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	class ButtonClickListener implements ActionListener { // 버튼 액션을 전달하는 클래스
		 public void actionPerformed (ActionEvent e) {
			username = Username.getText(); // TextField에 있는 사용자 이름 전달
			synchronized(LoginScreen.this) {
	         LoginScreen.this.notify();
	      }
			dispose(); // 창 닫기
		}
	}
	
	public String getUsername() { // 다른 메소드에서 사용자 이름 필요할 때 반환
      return username;
   }
}

class StoryScreen extends JFrame { // 초기 스토리 팝업을 위해 최초 1회 실행되는 GUI

    JTextArea textarea = new JTextArea(); 

    StoryScreen() {
        setTitle("Story");
        setLayout(new BorderLayout());
        textarea.setEditable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 400);
        setSize(1000, 300);
        setVisible(true);
        // 기초 GUI 설정
        JScrollPane scrollPane = new JScrollPane(textarea);
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton("Start Game");
        button.setPreferredSize(new Dimension(160, 30));
        button.addActionListener(new ButtonClickListener());
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.SOUTH);
        // GUI 세부 설정
        File storyfile = new File("story.txt"); // story.txt에서 파일입출력을 통해 스토리 출력
        try {
            Scanner story = new Scanner(storyfile);
            if (story.hasNextLine()) {
               String firstLine = story.nextLine();
               if (!firstLine.startsWith("")) {
                 throw new InvalidStoryFormatException();
               }
               textarea.append(firstLine + "\n");
            }

            while (story.hasNextLine()) {
               textarea.append(story.nextLine() + "\n");
            }
         } catch (FileNotFoundException e1) {
            textarea.append("\n <스토리 생략> (스토리 파일 없음)\n");
         } catch (InvalidStoryFormatException e2) {
            textarea.append("\n 적합한 스토리 파일이 아님");
         }
         // 버튼 누르기 전까지 대기
         synchronized (this) {
            try {
               wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
   }
   class ButtonClickListener implements ActionListener { // Start Game 버튼 눌림을 알리는 클래스
		public void actionPerformed (ActionEvent e) {
			synchronized(StoryScreen.this) {
	         StoryScreen.this.notify();
	      }
		dispose();
		}
   }
}

class GameScreen extends JFrame { // 실제로 게임이 진행되는 GUI
	JTextArea pollarea = new JTextArea(); // 그래프를 표기할 공간
	JTextArea eventarea = new JTextArea(); // 이벤트를 표기할 공간
	States states;
   Events events;
	GameScreen(States states, Events events) { // System으로부터 states와 events를 받아옴
      this.states = states; 
      this.events = events;
      setSize(1000, 400);
      // X11 자체의 문제로 크기가 증가하지 않음...
		setTitle("Joseon");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLocation(400, 400);
      setVisible(true);
      // 기초 GUI 설정
		JScrollPane scrollPane = new JScrollPane(eventarea);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(pollarea, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		JButton ybutton = new JButton("그리 하라");
		JButton nbutton = new JButton("아니 된다");
		panel.add(ybutton);
		panel.add(nbutton);
		add(panel, BorderLayout.SOUTH);
		// 세부 GUI 설정
		ybutton.setPreferredSize(new Dimension(160, 30));
      ybutton.addActionListener(new ButtonClickListener());
      // yes 버튼의 리스너 추가
      nbutton.setPreferredSize(new Dimension(160, 30));
      nbutton.addActionListener(new ButtonClickListener());
      // no 버튼의 리스너 추가
      // 초기 설정 시작
      eventarea.append("<나라의 상태를 설정합니다.>\n"); // 처음 나라의 상태를 설정할때 메세지 출력
      try{
        	Thread.sleep(500);
      }catch(Exception e){} //딜레이
      states.setstates();
      // 초기 설정 종료
      while(true){ // 반복 시작
         pollarea.setText(""); // 이전 내용을 모두 지우고 초기화
         eventarea.setText("");
         states.showstates(pollarea); // 새로운 통계 입력
         if(states.printstates(eventarea)==true){ // 엔딩 조건 불만족 시 계속 반복
            events.occurevent(eventarea); // 이벤트 발생
            synchronized (this) { // Y/N 버튼을 클릭하기 전까지 wait.
               try {
                   wait();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
            }
            try{ // 이벤트 결과 알 수 있게 2초간 대기
              	Thread.sleep(2000);
            }catch(Exception e){} //딜레이
         }
         else break;
      } // 반복 종료
      try{ // 게임 오버 시 원인 알수 있게 2초간 대기
        	Thread.sleep(2000);
      }catch(Exception e){} //딜레이
      dispose(); // 창 닫기
	}
	class ButtonClickListener implements ActionListener {
	   public void actionPerformed (ActionEvent e) {
	      if (e.getActionCommand().equals("그리 하라")) {
	         // Yes 버튼이 클릭된 경우의 처리
	        	states.changestates(eventarea,true,events.random1); // 상태 변화
	      } else if (e.getActionCommand().equals("아니 된다")) {
	         // No 버튼이 클릭된 경우의 처리
	        	states.changestates(eventarea,false,events.random1); // 상태 변화
	      }
	      synchronized (GameScreen.this) { // wait 풀기
	         GameScreen.this.notify();
	      }
	   }
	}
}

class EndScreen extends JFrame // 게임 종료 화면과 scoreboard를 출력하는 GUI
{
	JTextArea gameresult = new JTextArea();
	JTextArea scoreboard = new JTextArea();
	boolean isquit = false; // 게임 종료 버튼을 눌렀는지 여부를 main에게 넘겨줄 변수
	EndScreen(States states, String username)
	{
		setTitle("Game Result");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLocation(450, 200);
      setSize(500, 800);
      setVisible(true);
      // 기본 GUI 설정
		setLayout(new BorderLayout());
		add(gameresult, BorderLayout.NORTH);
		add(scoreboard, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		JButton restart = new JButton("다시 하기");
		restart.addActionListener(new ButtonClickListener(this));
		JButton quit = new JButton("게임 종료");
		quit.addActionListener(new ButtonClickListener(this));
		panel.add(restart);
		panel.add(quit);
		add(panel, BorderLayout.SOUTH);
      // 세부 GUI 설정
      // 이번 게임 결과표 출력
		String str = String.format("[통치기간 : %d대, %d년]\n",states.descendent, states.year);
		gameresult.append(str);
		str = String.format("[점수 : %d]\n",states.score);
		gameresult.append(str);
		// 스코어보드 출력 후 스코어보드 아래에 재시작 여부 질문
		printscore(scoreboard,username,states.score); // printsocre 함수를 call 하여 상위 10명의 점수 출력
		scoreboard.append("\n 게임을 다시 시작하시겠습니까?\n");
		synchronized (this) { // 버튼을 클릭하기 전까지 wait.
         try {
            wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
	   dispose(); // 창 닫기
	}
	class ButtonClickListener implements ActionListener { // 버튼 클릭을 감지하기 위한 클래스
		EndScreen parent; // 부모 field에 isquit 변수를 수정하기 위해 parent 지정

	   ButtonClickListener(EndScreen parent) { // 생성자
	      this.parent = parent;
	   }

	   public void actionPerformed(ActionEvent e) {
	      if (e.getActionCommand().equals("다시 하기")) {
	         // Yes 버튼이 클릭된 경우의 처리
	         parent.isquit = false; // isquit 값을 false로 설정
	      } else if (e.getActionCommand().equals("게임 종료")) {
	         // No 버튼이 클릭된 경우의 처리
	         parent.isquit = true; // isquit 값을 true로 설정
	      }
	      synchronized (EndScreen.this) { // wait 풀기
	         EndScreen.this.notify();
	      }
	   }
	}
	void printscore(JTextArea board, String name, int score) {
	      // 먼저 스코어보드에 있는 파일을 읽어옴. 유저명:점수 형식으로 되어 있음
	      try {
	         File scoreFile = new File("scoreboard.txt");
	         BufferedReader br = new BufferedReader(new FileReader(scoreFile));
	         ArrayList<Pair<String,Integer>> scores =  new ArrayList<>();
	         String line;
	         while((line = br.readLine()) != null) { // 끝까지 모든 결과를 읽음
	            String parts[] = line.split(":"); // : 단위로 쪼갬
	            String username = parts[0]; // 앞에는 사용자 이름이 옴
	            int userscore = Integer.parseInt(parts[1]); // 뒤에는 해당 사용자의 점수가 옴
	            scores.add(new Pair<>(username,userscore));
	         }
	         // 읽기 끝나면 새로운 점수 추가 후 정렬
	         // 점수가 너무 많아 스코어보드가 길어지는 것을 방지하기 위해 TOP 10 score만 남기기로 함
	         Pair<String,Integer> pair = new Pair<>(name,score);
	         scores.add(pair);
	         scores.sort((p1, p2) -> Integer.compare(p2.getY(), p1.getY()));
            // 정렬 완료
	         // 파일에 먼저 작성
	         BufferedWriter bw = new BufferedWriter(new FileWriter(scoreFile));
	         int cnt = 1;
	         for (Pair<String,Integer> p : scores)
	         {
	            if(cnt == 10)
	               break;
	            bw.write(p.getX() + ":" + p.getY());
	            bw.newLine();
	            cnt++;
	         }
	         bw.close(); // 폴더에 저장하기 위해서는 꼭 close 해줘야 함
            // 파일 작성 종료
            // 이제 scoreboard 창에 출력
	         for (Pair<String,Integer> p : scores)
	         {
	            if(p.equals(pair)) {
	               board.append("+)");
	            }
	            board.append(p.getX() + ":" + p.getY() + "\n");
	         }
            // 종료
	      }
	      catch(FileNotFoundException e) {
	         board.append("\n <스코어보드 없음> (스코어보드 파일 없음)\n");
	      }
	      catch(IOException e)
	      {
	         board.append("\n <IOException> (스코어보드 파일 오류)\n");
	      }
	      
	   }
	   
	   class Pair <T1, T2>{ // 점수 정렬을 위해서 선언한 클래스
	       private T1 x;
	       private T2 y;

	       Pair(T1 x, T2 y) {
	           this.x = x;
	           this.y = y;
	       }

	       public T1 getX(){
	           return x;
	       }

	       public T2 getY(){
	           return y;
	       }
	   }
}

class Systems{ // 게임 시스템을 담당
   String username = ""; // 사용자명
   void start(States states, Events events){ // 시작
    	LoginScreen login = new LoginScreen(); // 로그인 창
     	username = login.getUsername(); // 사용자명 가져옴
      GameScreen game = new GameScreen(states, events); // 게임 실행
   }
   boolean end(States states){ // 끝 (예외처리)
      EndScreen end = new EndScreen(states,username); // 종료 창 출력 및 종료 여부 판별
      return end.isquit; // 종료 여부를 main으로 전달
   }
}

class Statesbase{ // State 클래스의 상속을 위한 Super클래스 
   void ending(JTextArea area){
	  area.append("\n");
	  area.append("[엔딩 발생]");
	  area.append("\n");
   }
}

class States extends Statesbase{ // State 클래스 
   public int score; // 현재 점수
   public int descendent; // 몇 번째 왕인지
   public int year; 
   public int army;
   public int money;
   public int people;
   public int sadaebu; 
   
   void setstates() { //맨 처음 나라의 상태를 랜덤설정(GameScreen 실행 시 한 번만 실행 - 한 게임당 1회)
      army = (int)(Math.random()*41)+30;
      money = (int)(Math.random()*41)+30;
      people = (int)(Math.random()*41)+30;
      sadaebu = (int)(Math.random()*41)+30;
      descendent = 1; 
      score = 0;
   }
   
   void showstates(JTextArea pollArea){ // 나라의 상태 및 년도를 출력
      
      if(army<0) army=0;
      if(army>100) army=100;
      if(money<0) money=0;
      if(money>100) money=100;
      if(people<0) people=0;
      if(people>100) people=100;
      if(sadaebu<0) sadaebu=0;
      if(sadaebu>100) sadaebu=100;
      // 초과 시 최댓값으로 고정
      String str;
      str = String.format("[현재 점수 : %d]\n", score);
      pollArea.append(str);
      str = String.format("[제 %d대 왕 %d 년]\n",descendent,year);
      pollArea.append(str);
      str = String.format("<나라의 상태>\n");
      pollArea.append(str);
      // 현재 국가의 상태를 시각적으로 표시
      // 20블록에서 50블록으로 변경

      str = String.format(" [士] 충성  %4d : ",sadaebu);
      pollArea.append(str);
      for(int i=0;i<sadaebu/2;i++){
    	  pollArea.append("■");
      }
      for(int j=0;j<50-sadaebu/2;j++){
    	  pollArea.append("□");
      }
      pollArea.append("\n");
      
      str = String.format(" [民] 민심  %4d : ",people);
      pollArea.append(str);
      for(int i=0;i<people/2;i++){
    	  pollArea.append("■");
      }
      for(int j=0;j<50-people/2;j++){
    	  pollArea.append("□");
      }
      pollArea.append("\n");

      str = String.format(" [軍] 군사  %4d : ",army);
      pollArea.append(str);
      for(int i=0;i<army/2;i++){
         pollArea.append("■");
      }
      for(int j=0;j<50-army/2;j++){
    	  pollArea.append("□");
      }
      pollArea.append("\n");

      str = String.format(" [商] 경제  %4d : ",money);
      pollArea.append(str);
      for(int i=0;i<money/2;i++){
    	  pollArea.append("■");
      }
      for(int j=0;j<50-money/2;j++){
    	  pollArea.append("□");
      }
      pollArea.append("\n");
      
   }
   
   
   boolean printstates(JTextArea eventarea){ // 상태 수치를 체크하고 계속 진행 or 엔딩 출력
      
      boolean result=true; // 계속 진행할 지 여부 return
      String str = "";
      if(year == 40) { // 왕이 퇴임할 때가 되면 추가 점수를 받고 배율을 증가시킴
    	 str = String.format("제 %d대 왕은 나이가 들어 자리에서 물러나고 그의 아들이 자리에 올랐습니다.\n",descendent);
         eventarea.append(str);
         descendent += 1; // 배율 증가
         score += 1000; // 추가 점수 1000점
         year = 1; // 연도 초기화
         return result;
      }
      if ((army>0&&army<100)&&(money>0&&money<100)&&(people>00&&people<100)&&(sadaebu>0&&sadaebu<100)) { // 계속 진행 가능하다면
         year++;
         score += year * descendent; // 점수 증가시킴
      }
      else if(army<=0){
         result=false;
         super.ending(eventarea);
         eventarea.append("군사 수치가 0이 되었습니다.\n");
         eventarea.append("장군 : 적군이 성문까지 왔습니다! 우리에게는 이미 저들을 막을 만한 힘이 없습니다!\n");
         eventarea.append("순식간에 도성까지 왜구가 들어왔고, 왕은 끝내 살해당했습니다.\n");
      }
      else if(army>=100){
         result=false;
         super.ending(eventarea);
         eventarea.append("군사 수치가 100이 되었습니다.\n");
         eventarea.append("장군 : 모반이 일어났습니다! 전권을 제게 넘기십시오!\n");
         eventarea.append("모반을 일으킨 병사들은 나약한 왕을 폐위시켜 가두었고 왕은 얼마 못가 죽었습니다.\n");
      }
      else if(money<=0){
         result=false;
         super.ending(eventarea);
         eventarea.append("경제 수치가 0이 되었습니다.\n");
         eventarea.append("나라가 망했습니다. 극심한 기근으로 아무것도 남지 않았습니다.\n");
      }
      else if(money>=100){
         result=false;
         super.ending(eventarea);
         eventarea.append("경제 수치가 100이 되었습니다.\n");
         eventarea.append("왕이 궁궐 보수와 호화로운 연회에 빠진 사이, 상인들이 새로운 지배층이 되어 왕조가 무너졌습니다.\n");
      }
      else if(people<=0){
         result=false;
         super.ending(eventarea);
         eventarea.append("민심 수치가 0이 되었습니다.\n");
         eventarea.append("굶주림에 처한 백성들이 팔도에서 반란을 일으켰습니다. 더는 막을 수 없습니다!\n");
         eventarea.append("신하들도 뿔뿔이 흩어졌습니다. 나라는 혼란에 빠졌고 혁명으로 새로운 왕조가 탄생했습니다.\n");
      }
      else if(people>=100){
         result=false;
         super.ending(eventarea);
         eventarea.append("민심 수치가 100이 되었습니다.\n");
         eventarea.append("더는 왕을 두려워하지 않는 백성들이 유가적 질서를 무너트리기 위해 난을 일으켰습니다!\n");
         eventarea.append("궁궐이 불타고 있습니다. 왕은 도망치다 갈대밭에서 목숨을 잃었습니다.\n");
      }
      else if(sadaebu<=0){
         result=false;
         super.ending(eventarea);
         eventarea.append("충성 수치가 0이 되었습니다.\n");
         eventarea.append("역성혁명이다! 이 범부를 왕좌에서 끌어내려라!\n");
         eventarea.append("왕은 폐위되었고 대신들은 새로운 왕을 추대하였습니다.\n");
      }
      else if(sadaebu>=100){
         result=false;
         super.ending(eventarea);
         eventarea.append("충성 수치가 100이 되었습니다.\n");
         eventarea.append("이제 더는 상소를 읽으실 필요 없습니다. 저희가 알아서 하겠습니다.\n");
         eventarea.append("세도가들이 국정을 독점하기로 했습니다. 꼭두각시 왕을 만든 그들은 분열되어 서로 싸우기 시작했습니다.\n");
      }
      return result;
   }
   
   void changestates(JTextArea area, boolean input,int random1) { // 이벤트에 따라 상태를 변화시켜주고 그 결과를 출력 
      
      String symbol, temp;
      int value, count, loop=0;
      String str;
      area.append("\n");
      try{
         File eventfile = new File("event.txt");
         Scanner event = new Scanner(eventfile);
         
         for(count=0;count<random1;count++){
            event.nextLine();
         }
         // 만약 Y를 골랐다면 Y의 결과 출력
         if(input==true){
            
            while(true){
               temp=event.next();
               if(temp.endsWith("]")) break;
            }

            while(true){
               temp=event.next();
               area.append(temp+" ");
            if(temp.endsWith("]")) break;
            }
         
         
         }
         // N을 골랐다면 N의 결과 출력
         else{
            while(true){
               temp=event.next();
               if(temp.endsWith(",")) break;
            }
            
            while(true){
               temp=event.next();
               area.append(temp+" ");
            if(temp.endsWith("]")) break;
            }
         }
         area.append("\n");
         // 이벤트의 결과에 따라 값 변화 (무작위성 포함)
         while(loop==0){
            symbol = event.next();
         
            switch(symbol){
               case "軍":
                  value = event.nextInt() + (int)(Math.random()*20)-10;
                  army = army + value;
                  area.append(symbol +" "+value+"  ");
                  break;
               
               case "商":
                  value = event.nextInt() + (int)(Math.random()*20)-10;
                  money = money + value;
                  area.append(symbol +" "+value+"  ");
                  break;
               
               case "民":
                  value = event.nextInt() + (int)(Math.random()*20)-10;
                  people = people + value;
                  area.append(symbol +" "+value+"  ");
                  break;
                  
               case "士":
                  value = event.nextInt() + (int)(Math.random()*20)-10;
                  sadaebu = sadaebu + value;
                  area.append(symbol +" "+value+"  ");
                  break;
            
               case ",":
                  loop=1;
                  break;
               
               default:
            }
         }
         area.append("\n");
      }catch(FileNotFoundException e1){
    	  area.append("\n <프로그램 종료> (이벤트 파일 없음)\n");
         System.exit(0);
      }
   }
}

class Events{ // 이벤트 클래스 
   String event, result, icon, temp;
   int start = 0, end = 0, eventnum=0, count=0;
   static int random1 = 0;
   
   void occurevent(JTextArea area){ // 랜덤 이벤트 발생 
	  String str = "";
      try{
         Thread.sleep(1000);
         }catch(Exception e){} //딜레이
      area.append("<이벤트 발생>\n");
      try{
         Thread.sleep(2000);
         }catch(Exception e){} //딜레이
      eventnum=0;
      try{
         File eventfile = new File("event.txt");
         Scanner event = new Scanner(eventfile);
         
         while(event.hasNext()){ // 총 이벤트 수 계산
            if(event.next().equals("*"))
            eventnum++;
         }
         event = new Scanner(eventfile);
         
         random1 = (int)(Math.random()*eventnum); // 이벤트 수 범위 내에서 랜덤하게 고름
         for(count=0;count<random1;count++){
            event.nextLine();
         }
         str = String.format("[이벤트 NO.%d] \n",random1+1);
         area.append(str);     

         while(true){ // 첫 번째 괄호 읽음
            temp=event.next();
            if(temp.startsWith("[")) break;
         }
         area.append(temp);
         
         while(true){ // 두 번째 괄호 읽음
            temp=event.next();
            if(temp.startsWith("[")) break;
            area.append(" "+temp);
         }
         area.append("\n");
         
      }catch(FileNotFoundException e1){
    	  area.append("\n <프로그램 종료> (이벤트 파일 없음)\n");  // event.txt 파일이 없으면 종료
         System.exit(0);
      }
   }
   
}

class InvalidStoryFormatException extends Exception {
   public InvalidStoryFormatException( ) {
      super();
   }
}