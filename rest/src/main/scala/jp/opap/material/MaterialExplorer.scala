package jp.opap.material

import java.util
import javax.servlet.DispatcherType

import com.mongodb.MongoClient
import io.dropwizard.Application
import io.dropwizard.jackson.Jackson
import io.dropwizard.setup.{Bootstrap, Environment}
import jp.opap.material.dao.MongoProjectDao
import jp.opap.material.data.JavaScriptPrettyPrinter.PrettyPrintFilter
import jp.opap.material.data.JsonSerializers.AppSerializerModule
import jp.opap.material.facade.ProjectCollectionFacade
import jp.opap.material.resource.RootResource
import org.eclipse.jetty.servlets.CrossOriginFilter

object MaterialExplorer extends Application[AppConfiguration] {
  def main(args: Array[String]): Unit = {
    run(args:_*)
  }

  override def initialize(bootstrap: Bootstrap[AppConfiguration]): Unit = {
    val om = Jackson.newMinimalObjectMapper()
      .registerModule(AppSerializerModule)
    bootstrap.setObjectMapper(om)
  }

  override def run(configuration: AppConfiguration, environment: Environment): Unit = {
    val dbClient = new MongoClient(configuration.dbHost)
    val db = dbClient.getDatabase("material_explorer")

    val projectDao = new MongoProjectDao(db)
    val projectCollectionFacade = new ProjectCollectionFacade(projectDao)
    val rootResource = new RootResource(projectDao)

    val server = environment.jersey()
    server.register(rootResource)
    server.register(PrettyPrintFilter.SINGLETON)

    val servlets = environment.servlets()

    val cors = servlets.addFilter("CORS", classOf[CrossOriginFilter])
    cors.setInitParameter("allowedOrigins", "*")
    cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")
    cors.addMappingForUrlPatterns(util.EnumSet.allOf(classOf[DispatcherType]), true, "/*")

    // JSON Pretty Print
    servlets.addFilter(classOf[PrettyPrintFilter].getSimpleName, PrettyPrintFilter.SINGLETON)
      .addMappingForUrlPatterns(util.EnumSet.allOf(classOf[DispatcherType]), true, "/*")

    this.updateProjectData(projectCollectionFacade, configuration)
  }

  def updateProjectData(facade: ProjectCollectionFacade, configuration: AppConfiguration): Unit = {
    facade.updateProjects(Seq(("https://gitlab.com/", "kosys")), configuration)
  }
}
