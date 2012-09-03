package org.vertx.java.test.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.test.utils.DeploymentUtils;

public class VertxExternalResource extends ExternalResource {

  private File modDir;

  private VerticleManager manager;

  private Description description;

  private Map<Annotation, String> methodDeployments;

  public VertxExternalResource(VerticleManager manager) {
    this.manager = manager;
  }

  @Override
  public Statement apply(Statement base, Description description) {

    this.description = description;

    String vertxMods = System.getProperty("vertx.mods");
    this.modDir = new File(vertxMods);
    if (!modDir.exists()) {
      modDir.mkdirs();
    }

    System.out.printf("VertxExternalResource.apply(%s,%s)%n", base, description);
    return super.apply(base, description);
  }

  @Override
  protected void before() throws Throwable {
    this.methodDeployments = JUnitDeploymentUtils.deploy(manager, modDir, description);
    super.before();
  }

  @Override
  protected void after() {
    System.out.printf("VertxExternalResource.after()%n");
    if (methodDeployments.size() > 0) {
      DeploymentUtils.undeploy(manager, methodDeployments);
    }
    super.after();
  }

}
