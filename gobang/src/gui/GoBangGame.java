package gui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class GoBangGame extends JFrame implements IChessboard {
	//棋盘15*15
	private static final int maxX = 14;
	private static final int maxY = 14;
	//游戏当前状态，默认为ready
	private int currentMode = GameStatus.READY;
	
	private int playerTime = 1800;
	private int robotTime = 1800;
	private String title = "五子棋";
	
	private List<Point> allFreePoints = new ArrayList<Point>();
	
	private Image imageBG;//背景图片对象
	private Image imageBlack;//黑色棋子图片对象
	private Image imageWhite;//白色棋子图片对象
	private Image imageMenu;//菜单图片对象
	private Image imageW;//没有笑的绿豆蛙图片对象
	private Image imageX;//在笑的绿豆蛙图片对象
	private Image imageRim;//红色框图片对象
	
	private boolean isGameBegin = false;
	private boolean isGameOver = true;
	private boolean isUndo = false;
	
	private Color[][] allChesses = new Color[14][14];
	private static final IPlayer human = new HumanPlayer();
	private static BaseComputerAi robot = new BaseComputerAi();
	
	public GoBangGame() {
//		this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(GoBangGame.class.getResource("pointer.png")), new Point(0,0), ""));
		Applet.newAudioClip(GoBangGame.class.getResource("bg.mid")).loop();
		URL imgUrl1 = GoBangGame.class.getResource("bg_game.JPG");// 获取图片资源的路径
		imageBG = Toolkit.getDefaultToolkit().getImage(imgUrl1);// 获取图片资源
		
		URL imgUrl2 = GoBangGame.class.getResource("black.png");// 获取图片资源的路径
		imageBlack= Toolkit.getDefaultToolkit().getImage(imgUrl2);// 获取图片资源
		
		URL imgUrl3 = GoBangGame.class.getResource("white.png");// 获取图片资源的路径
		imageWhite = Toolkit.getDefaultToolkit().getImage(imgUrl3);// 获取图片资源
		
		URL imgUrl4 = GoBangGame.class.getResource("menu.png");// 获取图片资源的路径
		imageMenu = Toolkit.getDefaultToolkit().getImage(imgUrl4);// 获取图片资源
		
		URL imgUrl5 = GoBangGame.class.getResource("W.png");// 获取图片资源的路径
		imageW = Toolkit.getDefaultToolkit().getImage(imgUrl5);// 获取图片资源
		
		URL imgUrl6 = GoBangGame.class.getResource("X.gif");// 获取图片资源的路径
		imageX = Toolkit.getDefaultToolkit().getImage(imgUrl6);// 获取图片资源
		
		URL imgUrl8 = GoBangGame.class.getResource("rim.png");// 获取图片资源的路径
		imageRim = Toolkit.getDefaultToolkit().getImage(imgUrl8);// 获取图片资源
		
		this.setSize(700, 550);
		setWindowCenter();
		
		final GameCanvas canvas = new GameCanvas();
		this.getContentPane().add(canvas);
		this.setTitle(title);//设置标题
		this.setResizable(false);//设置不能改表窗囗大小
		this.setVisible(true);//设置显示窗囗
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭窗囗后的操作,关闭窗囗后结束进程	
		
		canvas.addMouseListener(new MouseInputAdapter(){
			public void mousePressed(MouseEvent e) {
				if((e.getX() >=179 && e.getX()<=652) 
						&& (e.getY()>=30&&e.getY()<=497)
						&& isGameBegin == true) {
					int row = (int)((e.getY() - 30)/33.4);
					int col = (int)((e.getX() - 179)/33.8);
					
					//如果在该棋盘位置等于空就表示该位置没有下棋可以走棋
					if(allChesses[row][col] == null) {
						allChesses[row][col] = Color.white;
						canvas.repaint(); 
					} //走棋完毕
					
					Point point = new Point(col, row);
					human.getMyPoints().add(point);
					
					if(!human.hasWin()) {
						setRobotRun(point);
					} else {
						JOptionPane.showMessageDialog(null,"You are winner,游戏结束！");
						resetGame();
						canvas.repaint();
					}
					
				} else {
					if((e.getX()>=17&&e.getX()<=39)&&(e.getY()>=249&&e.getY()<=289)){  //如果坐标在该区域表示单击了开始
						
						resetGame();
						canvas.repaint();
						JOptionPane.showMessageDialog(null,"可以开始了！");
						
						System.out.println("单击了开始");
					}
				}
			}
			
			/**
			 * 机器人下棋
			 * @param humanPoint 人的最后一步棋子
			 */
			private void setRobotRun(Point humanPoint) {
				
				try {
					robot.cljRun(human.getMyPoints(), humanPoint);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
//				robot.run(human.getMyPoints(), humanPoint);
				
				int robotChessSize = robot.getMyPoints().size();
				Point robotPoint = robot.getMyPoints().get(robotChessSize - 1);
				allChesses[robotPoint.y][robotPoint.x] = Color.black;
				canvas.repaint(); 
				
				if(robot.hasWin()) {
					JOptionPane.showMessageDialog(null,"机器人获胜，游戏结束！");
					resetGame();
					canvas.repaint();
				}
				
				System.out.println("robot row [" + robotPoint.x + "] col [" + robotPoint.y + "]");
			}
		});
	}
	
	/**
	 * 重置游戏
	 */
	private void resetGame() {
		for(int row = 0; row<allChesses.length; row ++) {
			for(int col = 0; col < allChesses.length; col ++) {
				allChesses[row][col]=null;
				Point point = new Point(row,col);
				allFreePoints.add(point);
			}
		}
		robot.setChessboard(this);
		human.setChessboard(this);
		isGameBegin = true;
		isGameOver = false;
	}
	
	private void setWindowCenter() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(this.getWidth() > screenSize.width || this.getHeight()>screenSize.height) {
			this.setLocation(0,0);
		} else {
			this.setLocation((screenSize.width-this.getWidth())/2,(screenSize.height-this.getHeight())/2);
		}
	}
	
	class GameCanvas extends JPanel {
		
		
		public void paint(Graphics g) {
			BufferedImage bImage = new BufferedImage(700,550,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = bImage.createGraphics();
			g2.drawImage(imageBG, 0, 0, this.getWidth(), this.getHeight(), this);
			g2.drawImage(imageMenu, 6, 242, 140, 52, this);
			g2.drawImage(imageW, 26, 42, imageW.getWidth(this), imageW.getHeight(this), this);
			g2.drawImage(imageW, 26, 307, imageW.getWidth(this), imageW.getHeight(this), this);
			g2.setColor(Color.white);
			
			
//			drawCountDownTime(g2);
			
			//画棋子
			drawChesses(g2);
			
			//将BufferedImage画到JPanel中
			g.drawImage(bImage, 0, 0, this);
		}

		private void drawCountDownTime(Graphics2D g2) {
			int minute = playerTime / 60;
			int second = playerTime % 60;
			String minuteStr = minute < 10 ? "0"+minute:minute +"";
			String secondStr = second < 10 ? "0"+second:second +"";
			g2.drawString(minuteStr+":"+secondStr,77,209);//在BufferedImage中绘制时间字符串
		}

		public void drawChesses(Graphics2D g2) {
			for(int row = 0; row<allChesses.length; row++) {
				for(int col=0; col<allChesses.length; col++) {
					if(allChesses[row][col]!=null){//如果某一个元素不为空就表示该位置有棋子
						if(allChesses[row][col]==Color.black){//如果元素值是black就在相应的位置绘制黑色的棋子
							g2.drawImage(imageBlack, (int)((179+col*34.8)+2), (int)((30+row*33.4)+2),31,31, this);
						}                   //列与列之间的距离是34.8,j*34.8就表示有多少个34.8得到长度，在加上179得到该棋子的x坐标 
						else{//否则是白色在相应位置画上棋子
							g2.drawImage(imageWhite, (int)((179+col*34.8)+2), (int)((30+row*33.4)+2),31,31, this);
						}
					}
				}
			}
		}
		
		public void drawChesses(int row,int col, int chessType,Graphics2D g2){
			Image chessImage = (chessType == ChessType.BLACK) ? imageBlack : imageWhite;
			g2.drawImage(chessImage, (int)((179+col*34.8)+2), (int)((30+row*33.4)+2),31,31, this);
		}
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}

	@Override
	public List<Point> getFreePoints() {
		return allFreePoints;
	}
	
	public static void main(String[] args) {
		new GoBangGame();
	}

}
