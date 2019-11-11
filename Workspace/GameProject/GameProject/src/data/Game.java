package data;

import static helpers.Artist.QuickLoad;
import static helpers.Artist.*;
import static helpers.Artist.TILE_SIZE;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

import UI.Button;
import UI.UI;
import UI.UI.Menu;
import helpers.Clock;
import helpers.SimpleAudioPlayer;
import helpers.StateManager;

import static helpers.SimpleAudioPlayer.*;

public class Game {

	private TileGrid grid;
	private Player player;
	private WaveManager waveManager;
	private UI gameUI;
	private Texture menuBackGround;
	private Enemy[] enemyTypes;
	private Enemy[] enemisBoss;
	private boolean back_menu = false, replay = false;
	private int waveNumber = 1, start;
	private int indexMap;
	private int soLuongQuan = 3;
	private float thoiGian = 3.15f;
	private boolean nextMap = false, priviousMap = false, startGame = false;

	public Game(TileGrid grid, int indexMap) {
		this.grid = grid;
		enemyTypes = new Enemy[3];
		enemisBoss = new Enemy[1];
		enemyTypes[0] = new EnemyAlien(grid.getXYStart().x, grid.getXYStart().y, grid);
		enemyTypes[1] = new EnemyUFO(grid.getXYStart().x, grid.getXYStart().y, grid);
		enemyTypes[2] = new EnemyPlane(grid.getXYStart().x, grid.getXYStart().y, grid);
		enemisBoss[0]	= new EnemyBoss(grid.getXYStart().x, grid.getXYStart().y, grid);
		waveManager = new WaveManager(enemyTypes, (int)thoiGian, soLuongQuan);
		player = new Player(grid, waveManager);
		player.setup();
		this.menuBackGround = QuickLoad("menu_background_2");
		this.indexMap = indexMap;
		setupUI();
	}

	private void setupUI() {
		gameUI = new UI();
		gameUI.addButton("TowerNormal", "TowerNormalFull", 815, 50, 50, 50);
		gameUI.addButton("TowerSniper", "TowerSniperFull", 875, 50, 50, 50);
		gameUI.addButton("TowerMachine", "TowerMachineFull", 935, 50, 50, 50);
		
		gameUI.addButton("Start", "start", 820, 330, 80, 60);
		gameUI.addButton("Pause", "pause", 910, 330, 80, 60);

		gameUI.addButton("Menu", "HomeMenu", 820, 510, 300, 50);

		gameUI.addButton("BackMap", "back", 820, 420, 80, 80);
		gameUI.addButton("NextMap", "next", 920, 420, 80, 80);
	}

	private void updateUI() {
		gameUI.draw();
		gameUI.drawStringSmall(815, 110, "Normal");
		gameUI.drawStringSmall(875, 110, "Sniper");
		gameUI.drawStringSmall(935, 110, "Machine");
		gameUI.drawString(820, 150, "MAP: " + (indexMap + 1));
		gameUI.drawString(840, 200, "Lives: " + player.Lives);
		gameUI.drawString(840, 240, "Cash: $" + player.Cash);
		gameUI.drawString(840, 280, "Wave " + waveNumber + "/10");
		gameUI.drawStringSmall(0, 0, StateManager.framesInLastSecond + " fps");
		

		if (Mouse.next()) {
			boolean mouuseClicked = Mouse.isButtonDown(0);
			if (mouuseClicked) {
				if (gameUI.isButtonClicked("TowerNormal")) // Bat su kien khi click vao button
					player.pickTower(new TowerSpecies(TowerType.TowerNormal, grid.getTile(0, 0),
							waveManager.getCurrentWave().getEnemyList()));
				if (gameUI.isButtonClicked("TowerSniper")) // Bat su kien khi click vao button
					player.pickTower(new TowerSpecies(TowerType.TowerSniper, grid.getTile(0, 0),
							waveManager.getCurrentWave().getEnemyList()));
				if (gameUI.isButtonClicked("TowerMachine")) // Bat su kien khi click vao button
					player.pickTower(new TowerSpecies(TowerType.TowerMachine, grid.getTile(0, 0),
							waveManager.getCurrentWave().getEnemyList()));
				if (gameUI.isButtonClicked("Menu")) {
					back_menu = true;
				}
				if (gameUI.isButtonClicked("Start")) {
					Clock.setMultiplier(1);
					start = 1;
					startGame = true;
				} else if (gameUI.isButtonClicked("Pause") && startGame == true) {
					Clock.setMultiplier(0);
					startGame = false;
				}
				if (gameUI.isButtonClicked("NextMap") && waveNumber == 0) {
					nextMap = true;
				}
				if (gameUI.isButtonClicked("BackMap") && waveNumber == 0) {
					priviousMap = true;
				}
			}
		}
	}

	public void update() {
		DrawQuadTex(menuBackGround, 800, 0, 200, 600);

		grid.draw();
		if (start == 0) {
			Clock.setMultiplier(0);
		}
		if (waveManager.isComplete() == false) {
			waveManager.update();
		} else {
			waveNumber++;
			if (waveNumber == 5) {
				waveManager = new WaveManager(enemisBoss, 1, 1);
			} else if (waveNumber == 10) {
				waveManager = new WaveManager(enemisBoss, 2, 3);
			} else {
				soLuongQuan = soLuongQuan + 2;
				thoiGian -= 0.15;
				System.out.println(thoiGian);
				waveManager = new WaveManager(enemyTypes, (int)thoiGian, soLuongQuan);
			}
			Clock.setMultiplier(0);
			player.setWaveManager(waveManager);
		}

		player.update();
		updateUI();

		if (GameLose() || GameWin()) {
			if (GameLose()) {
				Clock.setMultiplier(0);
				gameUI.addButton("GameOver", "lose", 240, 230, 800, 200);
				gameUI.addButton("Replay", "replay", 275, 400, 140, 50);
				gameUI.addButton("BackMenu", "HomeMenu", 550, 400, 200, 50);

				if (Mouse.isButtonDown(0)) {
					if (gameUI.isButtonClicked("Replay")) {
						setGameReplay(true);
					}
					if (gameUI.isButtonClicked("BackMenu")) {
						setBackMenu(true);
					}
				}
			}
			if (GameWin()) {
				Clock.setMultiplier(0);
				gameUI.addButton("GameWin", "victory", 240, 230, 800, 200);
				gameUI.addButton("Replay", "replay", 275, 400, 140, 50);
				gameUI.addButton("nextMapWin", "nextMap", 550, 400, 200, 50);

				if (Mouse.isButtonDown(0)) {
					if (gameUI.isButtonClicked("Replay")) {
						setGameReplay(true);
					}
					if (gameUI.isButtonClicked("nextMapWin")) {
						setNextMap(true);
					}
				}
			}
		}
	}

	public boolean GameLose() {
		if (player.getGameLose())
			return true;
		return false;
	}

	public boolean GameWin() {
		if (waveNumber > 10) // player.livesCount
			return true;
		return false;
	}

	public boolean getBackMenu() {
		return back_menu;
	}

	public void setBackMenu(boolean back_menu) {
		this.back_menu = back_menu;
	}

	public boolean getNextMap() {
		return nextMap;
	}

	public boolean getPriviousMap() {
		return priviousMap;
	}

	public void setNextMap(boolean nextMap) {
		this.nextMap = nextMap;
	}

	public void setPriviousMap(boolean priviousMap) {
		this.priviousMap = priviousMap;
	}

	public boolean getGameReplay() {
		return replay;
	}

	public void setGameReplay(boolean replay) {
		this.replay = replay;
	}

}
