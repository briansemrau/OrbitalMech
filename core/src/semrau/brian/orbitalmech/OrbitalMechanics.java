package semrau.brian.orbitalmech;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import semrau.brian.orbitalmech.system.*;

import static com.badlogic.gdx.Gdx.input;

public class OrbitalMechanics extends ApplicationAdapter implements InputProcessor {

	// Rendering

	private OrthographicCamera camera;

	private SpriteBatch batch;
	private BitmapFont font;

	private Particle focusedParticle;

	// Simulation

	private Simulation simulation;
	private float simSpeed = 1;
	private float stepsToRun;

	// Input

	private int mouseX, mouseY;

	// GUI

	private Stage stage;
	private Skin skin;

	private Table controlsTable;

	private TextButton regenerateButton;

	private Slider sunSlider;
	private Slider planetsSlider;
	private Slider pMassSlider;
	private Slider pMassVarSlider;
	private Slider pRadSlider;
	private Slider pRadVarSlider;
	private Slider moonsSlider;
	private Slider mMassSlider;
	private Slider mMassVarSlider;
	private Slider mRadSlider;
	private Slider mRadVarSlider;

	// Other

	private boolean showChart;

	@Override
	public void create() {
		// Init

		initRendering();
		initGui();

		// Simulation init

		simulation = new Simulation();
		generateSetup();

		// Control input

		InputMultiplexer im = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(im);
	}

	private void initRendering() {
		camera = new OrthographicCamera(800, 600);
		camera.position.set(-400, -300, 0);

		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("font/arial-15.fnt"));
	}

	private void initGui() {
		skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
		stage = new Stage(new ScreenViewport());

		controlsTable = new Table();
		controlsTable.setFillParent(true);
		controlsTable.align(Align.left | Align.top);

		regenerateButton = new TextButton("Regenerate", skin, "default");
		regenerateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generateSetup();
				event.stop();
			}
		});

		// Sun

		sunSlider = new Slider(1000000, 50000000, 1, false, skin);

		// Planets

		planetsSlider = new Slider(1, 10, 1, false, skin);
		pMassSlider = new Slider(0, 30000, 1, false, skin);
		pMassVarSlider = new Slider(0, 30000, 1, false, skin);
		pRadSlider = new Slider(0, 20000, 1, false, skin);
		pRadVarSlider = new Slider(0, 30000, 1, false, skin);

		// Moon

		moonsSlider = new Slider(0, 10, 1, false, skin);
		mMassSlider = new Slider(0, 1, .01f, false, skin);
		mMassVarSlider = new Slider(0, 500, 1, false, skin);
		mRadSlider = new Slider(0, 1000, 1, false, skin);
		mRadVarSlider = new Slider(0, 1000, 1, false, skin);

		// Set default values

		sunSlider.setValue(15000000);
		planetsSlider.setValue(9);
		pMassSlider.setValue(15000);
		pMassVarSlider.setValue(15000);
		pRadSlider.setValue(10000);
		pRadVarSlider.setValue(30000);
		moonsSlider.setValue(4);
		mMassSlider.setValue(1.0f / 30.0f);
		mMassVarSlider.setValue(100);
		mRadSlider.setValue(100);
		mRadVarSlider.setValue(300);

		// Building the table
		controlsTable.pad(30);

//        controlsTable.add(new Label("Sun Mass", skin));
//        controlsTable.row();
//        controlsTable.add(sunSlider).width(300).height(50);
//        controlsTable.row();
//
//        controlsTable.add(new Label("Number of Planets", skin));
//        controlsTable.add(new Label("Min Mass", skin));
//        controlsTable.add(new Label("Mass Variation", skin));
//        controlsTable.row();
//        controlsTable.add(planetsSlider).width(300).height(50);
//        controlsTable.add(pMassSlider).width(300).height(50);
//        controlsTable.add(pMassVarSlider).width(300).height(50);
//        controlsTable.row();
//
//        controlsTable.add(new Label("Planet Min Radius", skin));
//        controlsTable.add(new Label("Radius Variation", skin));
//        controlsTable.row();
//        controlsTable.add(pRadSlider).width(300).height(50);
//        controlsTable.add(pRadVarSlider).width(300).height(50);
//        controlsTable.row();
//
//        controlsTable.add(new Label("Maximum Moons", skin));
//        controlsTable.add(new Label("% Planet Mass", skin));
//        controlsTable.row();
//        controlsTable.add(moonsSlider).width(300).height(50);
//        controlsTable.add(mMassSlider).width(300).height(50);
//        controlsTable.row();
//
//        controlsTable.add(new Label("Moon Min Radius", skin));
//        controlsTable.add(new Label("Radius Variation", skin));
//        controlsTable.row();
//        controlsTable.add(mRadSlider).width(300).height(50);
//        controlsTable.add(mRadVarSlider).width(300).height(50);
//        controlsTable.row();
//
//        controlsTable.add(regenerateButton).width(200).height(50);
		// more

		Sprite sprite = new Sprite(new Texture("gui/speed.png"));
		sprite.flip(true, false);
		ImageButton slowbutton = new ImageButton(new SpriteDrawable(sprite));
		slowbutton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				simSpeed--;
			}
		});

		ImageButton pausebutton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("gui/pause.png"))));
		pausebutton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				simSpeed = 0;
			}
		});

		Sprite sprite2 = new Sprite(new Texture("gui/speed.png"));
		ImageButton fastbutton = new ImageButton(new SpriteDrawable(sprite2));
		fastbutton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				simSpeed++;
			}
		});

		Table subtable = new Table();

		subtable.add(slowbutton, pausebutton, fastbutton);
		controlsTable.add(subtable);
		controlsTable.row();
		controlsTable.add(new Label("Use WASD to move camera, +/- to zoom", skin));
		controlsTable.row();
		controlsTable.add(new Label("Click on a body to follow it", skin));

		stage.addActor(controlsTable);
	}


	private void generateSetup() {
		simulation.particles().clear();

		//################# Big Bang
//        for (int i = 0; i < 1000; i++) {
//            Particle p = new Particle(new Vector2(
//                    ((float) Math.random()) * 100 + 400,
//                    ((float) Math.random()) * 100 + 300),
//                    (float) Math.random() * 100);
//            float v = 10;
//            p.setVelocity(new Vector2(
//                    ((float) Math.random()) * v - v / 2,
//                    ((float) Math.random()) * v - v / 2));
//            simulation.particles().add(p);
//        }

//        for (int mouseX = 0; mouseX < 30; mouseX++) {
//            for (int mouseY = 0; mouseY < 30; mouseY++) {
//                Particle p = new Particle(new Vector2(mouseX * 10, mouseY * 10), (float) Math.random() * 100);
//                float v = 0;
//                p.setVelocity(new Vector2(
//                        ((float) Math.random()) * v - v / 2,
//                        ((float) Math.random()) * v - v / 2));
//                simulation.particles().add(p);
//            }
//        }

		//################# Polymers
//        float radius = 100;
//        for (float a = 0; a < Math.PI * 2; a += Math.PI / 50) {
//            Particle p = new Particle(new Vector2((float) Math.cos(a) * radius, (float) Math.sin(a) * radius), 50);
//            simulation.particles().add(p);
//        }

//        for (float r = 3; r < 120; r += 3) {
//            float interval = 36.0f / (2.0f * (float) Math.PI * r);
//            for (float a = 0; a <= Math.PI * 2 - interval; a += interval) {
//                Particle p = new Particle(new Vector2((float) Math.cos(a) * r, (float) Math.sin(a) * r), 9);
//                simulation.particles().add(p);
//            }
//        }

		Particle sun = new Particle(new Vector2(0, 0), sunSlider.getValue());
		sun.setColor(Color.ORANGE);
		simulation.particles().add(sun);

		focusedParticle = sun;

		for (int i = 0; i < planetsSlider.getValue(); i++) {

			// Put planet at random polar coordinates
			float angle = (float) Math.PI * 2 * (float) Math.random();
			Particle planet = new Particle(
					new Vector2(sun.radius() + (float) Math.random() * pRadVarSlider.getValue() + pRadSlider.getValue(), 0)
							.setAngleRad(angle),
					(float) Math.random() * pMassVarSlider.getValue() + pMassSlider.getValue());

			// Set planet close to a circular orbit
			// F=mv^2/r
			planet.setVelocity(
					new Vector2(
							(float) Math.sqrt(Simulation.G * sun.mass() / (planet.pos().dst(sun.pos()) * 4.0f)),
							0).setAngleRad(angle - (float) Math.PI / 2));

			simulation.particles().add(planet);

			for (int j = 0; j < moonsSlider.getValue() * Math.random(); j++) {

				float moonAngle = (float) Math.PI * 2 * (float) Math.random();
				Particle moon = new Particle(
						planet.pos().cpy()
								.add(new Vector2(planet.radius() + (float) Math.random() * mRadVarSlider.getValue() + mRadSlider.getValue(), 0)
										.setAngleRad(moonAngle)),
						(float) Math.random() * planet.mass() * mMassSlider.getValue());
				// Set moon close to a circular orbit
				// F=mv^2/r
				moon.setVelocity(planet.velocity().cpy().add(
						new Vector2(
								(float) Math.sqrt(Simulation.G * planet.mass() / (moon.pos().dst(planet.pos()) * 4.0f)),
								0).setAngleRad(moonAngle - (float) Math.PI / 2)));

				simulation.particles().add(moon);

			}

		}

//        for (int i = 0; i < 10; i++) {
//            Particle p = new Particle(new Vector2((float) Math.random() * 2000 - 1000, (float) Math.random() * 2000 - 1000), (float) Math.random() * 2000);
//            p.setVelocity(p.pos().cpy().setAngleRad(p.pos().angleRad() - (float) Math.PI / 2).setLength((float) Math.random() * 50 + 20));
//            simulation.particles().add(p);
//        }

	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		inputs(deltaTime);
		update(deltaTime);

		// render

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (focusedParticle != null)
			camera.position.set(focusedParticle.pos().x, focusedParticle.pos().y, 0);

		camera.update();

		simulation.drawParticles(camera);

		stage.draw();

		batch.begin();
		font.draw(batch, "particles:" + simulation.particles().size(), 0, 40);
		font.draw(batch, "fps:" + Gdx.graphics.getFramesPerSecond(), 0, 20);
		batch.end();

	}

	private void update(float deltaTime) {
		stage.act(deltaTime);

//        if (addingParticles) {
//            Vector3 unprojected = camera.unproject(new Vector3(mouseX, mouseY, 0));
//            simulation.particles().add(new Particle(new Vector2(unprojected.x, unprojected.y), (float) Math.random() * 100));
//        }

		// Simulate

		if (simSpeed != 0)
			stepsToRun += deltaTime * 60.0f * (float) Math.pow(2, Math.abs(simSpeed) - 1);

		float timeStep = 1.0f / 60.0f;

		if (simSpeed < 0)
			timeStep = -timeStep;

		for (; (int) stepsToRun > 0; stepsToRun--) {
			simulation.step(timeStep);
		}
	}

	private void inputs(float deltaTime) {

		float translateSpeed = 600 * camera.zoom * deltaTime;
		float zoomControl = 1.0f + 0.03f;
		if (input.isKeyPressed(Input.Keys.W)) {
			focusedParticle = null;
			camera.translate(0, translateSpeed);
		}
		if (input.isKeyPressed(Input.Keys.A)) {
			focusedParticle = null;
			camera.translate(-translateSpeed, 0);
		}
		if (input.isKeyPressed(Input.Keys.S)) {
			focusedParticle = null;
			camera.translate(0, -translateSpeed);
		}
		if (input.isKeyPressed(Input.Keys.D)) {
			focusedParticle = null;
			camera.translate(translateSpeed, 0);
		}
		if (input.isKeyPressed(Input.Keys.EQUALS)) {
			camera.zoom /= zoomControl;
		}
		if (input.isKeyPressed(Input.Keys.MINUS)) {
			camera.zoom *= zoomControl;
		}

		if (input.isKeyPressed(Input.Keys.R)) {
			generateSetup();
		}

	}

	@Override
	public void resize(int width, int height) {
//        System.out.println(width + " " + height);
		camera.setToOrtho(false, width, height);

		stage.getViewport().update(width, height, true);
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {

			case Input.Keys.O:
				showChart = !showChart;
				return true;

			case Input.Keys.COMMA:
				simSpeed--;
				return true;
			case Input.Keys.PERIOD:
				simSpeed++;
				return true;
			case Input.Keys.SPACE:
				simSpeed = 0;
				return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		this.mouseX = screenX;
		this.mouseY = screenY;

		Vector3 unproj = camera.unproject(new Vector3(screenX, screenY, 0));
		Vector2 coords = new Vector2(unproj.x, unproj.y);

		float minDist = Float.MAX_VALUE;
		Particle closest = null;
		for (Particle p : simulation.particles()) {
			float dist = p.pos().dst(coords);
			if (dist < minDist) {
				minDist = dist;
				closest = p;
			}
		}

		if (minDist < 500)
			focusedParticle = closest;
		else
			focusedParticle = null;

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.mouseX = screenX;
		this.mouseY = screenY;
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
