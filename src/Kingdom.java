// https://github.com/psw9808/Java-Term-Project 에서 추가 기능 + GUI를 이용해 시각화하기로 함
// 시대 배경 조선시대로 바꾸고 이벤트 추가함
// 점수 시스템 추가
// 스코어보드 시스템 추가
// GUI 적용 시작
// 스코어보드에 남을 이름을 입력하는 LoginScreen 창 추가

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
		String username = "";
		while(true){
			states.year=1;
			systems.start();
			
			System.out.println("<나라의 상태를 설정합니다.>");  // 처음 나라의 상태를 설정할때 메세지 출력
			try{
			Thread.sleep(1000);
			}catch(Exception e){} //딜레이
			System.out.println();
			states.setstates();
			
			while(true){
				states.showstates();
				if(states.printstates()==true){
					events.occurevent();
					states.changestates(events.inputs(),events.random1);
				}
				else break;
			}
			
			if(systems.end(states.year,states.descendent,states.score)==false){
				System.out.printf("\n <게임을 종료합니다.>\n");
				break;
			}
		}
	}
}
class LoginScreen extends JFrame{
	JTextField Username;
	String username;
	LoginScreen(){
		setTitle("Enter your username");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(2,1));
		setLocation(700,450);
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
		//setSize(300,300);
		setVisible(true);
		// 기다리기
        synchronized(this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	class ButtonClickListener implements ActionListener {
		 public void actionPerformed (ActionEvent e) {
			 username = Username.getText();
			 synchronized(LoginScreen.this) {
	             LoginScreen.this.notify();
	         }
			 dispose();
		 }
	}
	
	public String getUsername() {
        return username;
    }
}
class Systems{ // 시스템 클래스 (시작/끝) 
	String username = "";
	void start(){ // 시작 (파일 입출력,예외처리)
		LoginScreen login = new LoginScreen();
		username = login.getUsername();
		try{
			File storyfile = new File("story.txt");
			Scanner story = new Scanner(storyfile);
			
			System.out.println();
			while(story.hasNext()){
				System.out.println(story.nextLine());
			}
			System.out.println();
			story.close();
		}catch(FileNotFoundException e1){
			System.out.println("\n <스토리 생략> (스토리 파일 없음)\n");
		}
	}
	
	boolean end(int year, int descendent, int score){ // 끝 (예외처리)
		Scanner userinput = new Scanner(System.in);
		int input;
		int loop=0;
		
		System.out.printf("[통치기간 : %d대, %d년]\n", descendent, year);
		while(loop==0){
			try{
				System.out.println("[점수 : %d]\n", score);
				printscore(username, score);
				System.out.print("\n 	다음 왕으로 계속 진행하시겠습니까? ( 1: 네 / 2: 아니오 )  : ");
				input = userinput.nextInt();
				if (input!=1 && input!=2) throw new NotRightNumberException();
				else if(input == 1) {
					
					loop = 1;
				}
				else {
					
					loop = 2;
				}
			}catch(NotRightNumberException e2){
				System.out.println("\n <1과 2중에 하나를 입력하세요.>");
			}catch(InputMismatchException e3){
				System.out.println("\n <입력은 아라비아 숫자 1과 2중 하나로 하여야 합니다.>");
				userinput = new Scanner(System.in);
			}
		}
		if(loop==1) return true;
		else return false;
	}

	void printscore(String username, int userscore) {
	      // 먼저 스코어보드에 있는 파일을 읽어옴. 유저명:점수 형식으로 되어 있음
	      try {
	         File scoreFile = new File("scoreboard.txt");
	         BufferedReader br = new BufferedReader(new FileReader(scoreFile));
	         ArrayList<Pair<String,Integer>> scores =  new ArrayList<>();
	         String line;
	         while((line = br.readLine()) != null) {
	            String parts[] = line.split(":");
	            String username = parts[0];
	            int userscore = Integer.parseInt(parts[1]);
	            scores.add(new Pair<>(username,userscore));
	         }
	         // 읽기 끝나면 새로운 점수 추가 후 정렬
	         // 점수가 너무 많아 스코어보드가 길어지는 것을 방지하기 위해 TOP 10 score만 남기기로 함
	         Pair<String,Integer> pair = new Pair<>(username,userscore);
	         scores.add(pair);
	         scores.sort((p1, p2) -> Integer.compare(p2.getY(), p1.getY()));
	         // 이제 파일에 작성
	         BufferedWriter bw = new BufferedWriter(new FileWriter(scoreFile));
	         // 10개만 작성
	         int cnt = 1;
	         for (Pair<String,Integer> p : scores)
	         {
	            if(cnt == 10)
	               break;
	            bw.write(p.getX() + ":" + p.getY());
	            bw.newLine();
	            cnt++;
	         }
	         bw.close();
	         for (Pair<String,Integer> p : scores) // 이번에 추가된 점수에 +) 표기
	         {
	            if(p.equals(pair)) {
	               System.out.println("+)");
	            }
	            System.out.println(p.getX() + ":" + p.getY() + "\n");
	         }
	      }
	      catch(FileNotFoundException e) {
	         System.out.println("\n <스코어보드 없음> (스코어보드 파일 없음)\n");
	      }
	      catch(IOException e)
	      {
	         System.out.println("\n <IOException> (스코어보드 파일 오류)\n");
	      }
	      
	   }
	   
	   class Pair <T1, T2>{
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

class Statesbase{ // State 클래스의 상속을 위한 Super클래스 
	void ending(){
		System.out.println();
		System.out.println("[엔딩 발생]");
		System.out.println();
	}
}

class States extends Statesbase{ // State 클래스 
	public int score;
    public int descendent;
	public int year;
	public int army;
	public int money;
	public int people;
	public int sadaebu;
	
	void setstates(){ //맨 처음 나라의 상태를 랜덤설정
	army = (int)(Math.random()*41)+30;
	money = (int)(Math.random()*41)+30;
	people = (int)(Math.random()*41)+30;
	sadaebu = (int)(Math.random()*41)+30;
	descendent = 1; // 몇 번째 왕인지 
    score = 0; // 점수
	}
	
	void showstates(){ // 나라의 상태 및 년도를 출력
		
		if(army<0) army=0;
		if(army>100) army=100;
		if(money<0) money=0;
		if(money>100) money=100;
		if(people<0) people=0;
		if(people>100) people=100;
		if(sadaebu<0) sadaebu=0;
		if(sadaebu>100) sadaebu=100;
		
		System.out.printf("[Year %d]\n",year);
		System.out.printf("<나라의 상태>\n");
		System.out.printf(" [軍] 군사  %4d : ",army);	// 나라의 4가지 상태를 기호로 표현
		for(int i=0;i<army/5;i++){						// 나라의 상태를 시각적으로 표현하는 기능을 추가
			System.out.printf("■");
		}
		for(int j=0;j<20-army/5;j++){
			System.out.printf("□");
		}
		System.out.println();
		System.out.printf(" [商] 경제  %4d : ",money);
		for(int i=0;i<money/5;i++){
			System.out.printf("■");
		}
		for(int j=0;j<20-money/5;j++){
			System.out.printf("□");
		}
		System.out.println();
		System.out.printf(" [民] 민심  %4d : ",people);
		for(int i=0;i<people/5;i++){
			System.out.printf("■");
		}
		for(int j=0;j<20-people/5;j++){
			System.out.printf("□");
		}
		System.out.println();
		System.out.printf(" [士] 충성  %4d : ",sadaebu);
		for(int i=0;i<sadaebu/5;i++){
			System.out.printf("■");
		}
		for(int j=0;j<20-sadaebu/5;j++){
			System.out.printf("□");
		}
		System.out.println();
	}
	
	
	boolean printstates(){ // 상태 수치를 체크하고 계속 진행 or 엔딩 출력
		
		boolean result=true;
		String str = "";
      	if(year == 40) {
    		System.out.printf("제 %d대 왕은 나이가 들어 자리에서 물러나고 그의 아들이 자리에 올랐습니다.\n",descendent);
         	descendent += 1; // 배율 증가
         	score += 10000; // 추가 점수 10000점
         	year = 1; // 연도 초기화
			return result;
      	}
		if ((army>0&&army<100)&&(money>0&&money<100)&&(people>00&&people<100)&&(sadaebu>0&&sadaebu<100)){
			year++;
			score += year * descendent; // 점수 변동
		}
		
		else if(army<=0){
			result=false;
			super.ending();
			System.out.println("군사 수치가 0이 되었습니다.");
			System.out.println("장군 : 적군이 성문까지 왔습니다! 우리에게는 이미 저들을 막을 만한 힘이 없습니다!");
			System.out.println("순식간에 도성까지 왜구가 들어왔고, 왕은 끝내 살해당했습니다.");
		}
		
		else if(army>=100){
			result=false;
			super.ending();
			System.out.println("군사 수치가 100이 되었습니다.");
			System.out.println("장군 : 모반이 일어났습니다! 전권을 제게 넘기십시오!");
			System.out.println("모반을 일으킨 병사들은 나약한 왕을 폐위시켜 가두었고 왕은 얼마 못가 죽었습니다.");
		}
		
		else if(money<=0){
			result=false;
			super.ending();
			System.out.println("경제 수치가 0이 되었습니다.");
			System.out.println("나라가 망했습니다. 극심한 기근으로 아무것도 남지 않았습니다.");
		}
		
		else if(money>=100){
			result=false;
			super.ending();
			System.out.println("경제 수치가 100이 되었습니다.");
			System.out.println("왕이 궁궐 보수와 호화로운 연회에 빠진 사이, 상인들이 새로운 지배층이 되어 왕조가 무너졌습니다.");
		}
		
		else if(people<=0){
			result=false;
			super.ending();
			System.out.println("민심 수치가 0이 되었습니다.");
			System.out.println("굶주림에 처한 백성들이 팔도에서 반란을 일으켰습니다. 더는 막을 수 없습니다!");
			System.out.println("신하들도 뿔뿔이 흩어졌습니다. 나라는 혼란에 빠졌고 혁명으로 새로운 왕조가 탄생했습니다.");
		}
		
		else if(people>=100){
			result=false;
			super.ending();
			System.out.println("민심 수치가 100이 되었습니다.");
			System.out.println("더는 왕을 두려워하지 않는 백성들이 유가적 질서를 무너트리기 위해 난을 일으켰습니다!");
			System.out.println("궁궐이 불타고 있습니다. 왕은 도망치다 갈대밭에서 목숨을 잃었습니다.");
		}
		
		else if(sadaebu<=0){
			result=false;
			super.ending();
			System.out.println("충성 수치가 0이 되었습니다.");
			System.out.println("당신같은 폭군을 섬길 수는 없소! 자리에서 내려오시오!");
			System.out.println("왕은 폐위되었고 대신들은 새로운 왕을 추대하였습니다.");
		}
		else if(sadaebu>=100){
			result=false;
			super.ending();
			System.out.println("충성 수치가 100이 되었습니다.");
			System.out.println("이제 더는 상소를 읽으실 필요 없습니다. 저희가 알아서 하겠습니다.");
			System.out.println("세도가들이 국정을 독점하기로 했습니다. 꼭두각시 왕을 만든 그들은 분열되어 서로 싸우기 시작했습니다.");
		}
		return result;
	}
	
	void changestates(boolean input,int random1) { // 상태를 변화시켜주고 그 결과를 출력 
		
		String symbol,temp;
		int value,count,loop=0;
		
		System.out.println();
		try{
			File eventfile = new File("event.txt");
			Scanner event = new Scanner(eventfile);
				
			for(count=0;count<random1;count++){
				event.nextLine();
			}
			
			if(input==true){
				
				while(true){
					temp=event.next();
					if(temp.endsWith("]")) break;
				}

				while(true){
					temp=event.next();
					System.out.print(temp+" ");
				if(temp.endsWith("]")) break;
				}
			
			
			}
			else{
				while(true){
					temp=event.next();
					if(temp.endsWith(",")) break;
				}
				
				while(true){
					temp=event.next();
					System.out.print(temp+" ");
				if(temp.endsWith("]")) break;
				}
			}
			System.out.println();
			
				
			while(loop==0){
				symbol = event.next();
			
				switch(symbol){
					case "軍":
						value = event.nextInt() + (int)(Math.random()*20)-10;
						army = army + value;
						System.out.print(symbol +" "+value+"  ");
						break;
					
					 case "商":
						value = event.nextInt() + (int)(Math.random()*20)-10;
						money = money + value;
						System.out.print(symbol +" "+value+"  ");
						break;
					
					case "民":
						value = event.nextInt() + (int)(Math.random()*20)-10;
						people = people + value;
						System.out.print(symbol +" "+value+"  ");
						break;
						
					case "士":
						value = event.nextInt() + (int)(Math.random()*20)-10;
						sadaebu = sadaebu + value;
						System.out.print(symbol +" "+value+"  ");
						break;
				
					case ",":
						loop=1;
						break;
					
					default:
				}
			}
			System.out.println();
			System.out.println();
			event.close();
		}catch(FileNotFoundException e1){
			System.out.println("\n <프로그램 종료> (이벤트 파일 없음)\n");
			System.exit(0);
		}
	}
}

class Events{ // 이벤트 클래스 
	String event,result,icon,temp;
	int start = 0,end = 0,eventnum=0,count=0;
	static int random1=0;
	
	void occurevent(){ // 랜덤 이벤트 발생 
		try{
			Thread.sleep(1000);
			}catch(Exception e){} //딜레이
		System.out.println("\n<이벤트 발생>\n");
		try{
			Thread.sleep(1000);
			}catch(Exception e){} //딜레이
			
		eventnum=0;
		
		try{
			File eventfile = new File("event.txt");
			Scanner event = new Scanner(eventfile);
			
			while(event.hasNext()){
				if(event.next().equals("*"))
				eventnum++;
			}
			event.close();
			event = new Scanner(eventfile);
			
			random1 = (int)(Math.random()*eventnum);
			
			for(count=0;count<random1;count++){
				event.nextLine();
			}
			
			System.out.printf("[이벤트 NO.%d] \n",random1+1);  //이벤트 번호를 나타내는 기능을 추가
			
			
			while(true){
				temp=event.next();
				if(temp.startsWith("[")) break;
			}
			System.out.print(temp);
			
			while(true){
				temp=event.next();
				if(temp.startsWith("[")) break;
				System.out.print(" "+temp);
			}
			
			System.out.println();
			
		}catch(FileNotFoundException e1){
			System.out.println("\n <프로그램 종료> (이벤트 파일 없음)\n");  // event.txt 파일이 없으면 종료
			System.exit(0);
		}
	}
	
	boolean inputs(){ // 유저 입력 
		Scanner userinput = new Scanner(System.in);
		int input,loop=0;
		
		while(loop==0){
			try{
				System.out.printf("\n\t왕 ( 1: 그리 하거라 / 2: 아니된다 )  : ");
				input = userinput.nextInt();
				if (input!=1 && input!=2) throw new NotRightNumberException();
				else if(input == 1) {
					loop = 1;
					
				}
				else {
					loop = 2;
					
				}
			}catch(NotRightNumberException e2){
				System.out.println("\n <1과 2중에 하나를 입력하세요.>");
			}catch(InputMismatchException e3){
				System.out.println("\n <입력은 아라비아 숫자 1과 2중 하나로 하여야 합니다.>");
				userinput = new Scanner(System.in);
			}
		}
		
		if(loop==1) return true;
		else return false;
	}
	
}

class NotRightNumberException extends Exception{ //예외처리 
	NotRightNumberException(){
		super("1또는 2가 아닌 숫자 예외");
	}
}