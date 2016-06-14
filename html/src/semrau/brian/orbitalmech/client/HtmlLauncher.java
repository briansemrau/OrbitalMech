package semrau.brian.orbitalmech.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import semrau.brian.orbitalmech.OrbitalMechanics;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(720, 720);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new OrbitalMechanics();
        }
}