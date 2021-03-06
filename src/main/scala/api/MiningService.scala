package api

import akka.actor.{ActorRef, ActorSystem, Props}
import core.{CoordinatorActor, ExperimentActor, JobResults, RestMessage}
import dao.{JobResultsDAL, LdsmDAL}
import spray.http.MediaTypes._
import spray.http._
import spray.routing.Route

import eu.hbp.mip.messages.external._

object MiningService {

  // TODO Gather this information from all the containers
  val methods_mock =
  """
        {
            "algorithms": [
            {
                "code": "histograms",
                "label": "Histograms",
                "type": ["statistics"],
                "environment": "Python",
                "description": "Histograms...",
                "docker_image": "hbpmip/python-histograms:7be184a",
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": false,
                      "polynominal": false
                    }
                }
            },
            {
                "code": "statisticsSummary",
                "label": "Statistics Summary",
                "type": ["statistics"],
                "environment": "R",
                "description": "Statistics Summary...",
                "docker_image": "hbpmip/r-summary-stats:52198fd",
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": false,
                      "polynominal": false
                    }
                }
            },
            {
                "code": "linearRegression",
                "label": "Linear Regression",
                "type": ["statistics"],
                "docker_image": "hbpmip/r-linear-regression:52198fd",
                "environment": "R",
                "description": "Standard Linear Regression...",
                "parameters": [],
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": false,
                      "polynominal": false
                    },
                    "groupings": {
                        "min_count": 0,
                        "max_count": 1
                    },
                    "covariables": {
                        "min_count": 0,
                        "max_count": null
                    },
                    "mixed": true
                }
            },
            {
                "code": "anova",
                "label": "Anova",
                "type": ["statistics"],
                "docker_image": "hbpmip/r-linear-regression:52198fd",
                "environment": "R",
                "description": "ANOVA...",
                "parameters": [],
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": false,
                      "polynominal": false
                    },
                    "groupings": {
                        "min_count": 1,
                        "max_count": null
                    },
                    "covariables": {
                        "min_count": 0,
                        "max_count": null
                    },
                    "mixed": true
                }
            },
            {
                "code": "knn",
                "label": "K-nearest neighbors",
                "type": ["predictive_model"],
                "docker_image": "hbpmip/java-rapidminer-knn:latest",
                "environment": "Java/RapidMiner",
                "description": "K-nearest neighbors...",
                "parameters": [{
                    "code": "k",
                    "label": "k",
                    "default_value": 5,
                    "type": "int",
                    "constraints": {
                        "min": 1,
                        "max": null
                    },
                    "description": "The number of closest neighbours to take into consideration. Typical values range from 2 to 10."
                }],
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": true,
                      "polynominal": true
                    },
                    "groupings": {
                        "min_count": 0,
                        "max_count": 0
                    },
                    "covariables": {
                        "min_count": "1",
                        "max_count": null
                    },
                    "mixed": false
                }
            },
            {
                "code": "gpr",
                "label": "Gaussian Process Regression",
                "type": ["predictive_model"],
                "environment": "Java/GPJ",
                "disable": true
            },
            {
                "code": "svm",
                "label": "SVM",
                "type": ["predictive_model"],
                "environment": "Java/RapidMiner",
                "disable": true
            },
            {
                "code": "ffneuralnet",
                "label": "Feedforward Neural Network",
                "type": ["predictive_model"],
                "environment": "Java/RapidMiner",
                "disable": true
            },
            {
                "code": "randomforest",
                "label": "Random Forest",
                "type": ["predictive_model"],
                "environment": "Java/RapidMiner",
                "disable": true
            },
            {
                "code": "naiveBayes",
                "label": "Naive Bayes",
                "type": ["predictive_model"],
                "docker_image": "hbpmip/java-rapidminer-naivebayes:latest",
                "environment": "Java/RapidMiner",
                "description": "Naive Bayes...",
                "parameters": [],
                "constraints": {
                    "variable": {
                      "real": false,
                      "binominal": true,
                      "polynominal": true
                    },
                    "groupings": {
                        "min_count": 0,
                        "max_count": 0
                    },
                    "covariables": {
                        "min_count": 1,
                        "max_count": null
                    },
                    "mixed": false
                }
            },
            {
                "code": "tSNE",
                "label": "tSNE",
                "disable": true,
                "type": ["features_extraction"],
                "docker_image": "hbpmip/r-tsne:latest",
                "environment": "R",
                "description": "tSNE...",
                "parameters": [],
                "constraints": {
                    "variable": {
                      "real": true,
                      "binominal": true,
                      "polynominal": true
                    },
                    "groupings": {
                        "min_count": 0,
                        "max_count": 0
                    },
                    "covariables": {
                        "min_count": 1,
                        "max_count": null
                    },
                    "mixed": false
                }
            }
            ],
            "validations": [{
                "code": "kFoldCrossValidation",
                "label": "Random k-fold Cross Validation",
                "parameters": [{
                    "code": "fold",
                    "label": "Fold",
                    "default_value": 5,
                    "type": "int",
                    "constraints": {
                        "min": 2,
                        "max": 20
                    },
                    "description": "The number of cross-validation fold"
                }]
            }],
            "metrics": {
                "regression": [
                    {
                        "code": "MSE",
                        "label": "Mean square error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "RMSE" ,
                        "label": "Root mean square error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "MAE",
                        "label": "Mean absolute error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "R-squared",
                        "label": "Coefficient of determination (R²)",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Explained variance",
                        "label": "Explained variance",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    }
                ],
                "binominal_classification": [
                    {
                        "code": "Confusion matrix",
                        "label": "Confusion matrix",
                        "type": "confusion_matrix",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Accuracy",
                        "label": "Mean square error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Precision",
                        "label": "Root mean square error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Sensitivity",
                        "label": "Mean absolute error",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "False positive rate",
                        "label": "False positive rate",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    }
                ],
                "classification": [
                    {
                        "code": "Confusion matrix",
                        "label": "Confusion matrix",
                        "type": "confusion_matrix",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Accuracy",
                        "label": "Accuracy",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Weighted Precision",
                        "label": "Weighted Precision",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Weighted Recall",
                        "label": "Weighted Recall",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Weighted F1-score",
                        "label": "Weighted F1-score",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    },
                    {
                        "code": "Weighted false positive rate",
                        "label": "Weighted false positive rate",
                        "type": "numeric",
                        "tooltip": "To be completed"
                    }
                ]
            }
        }
  """
}

// this trait defines our service behavior independently from the service actor
class MiningService(val chronosService: ActorRef,
                    val resultDatabase: JobResultsDAL,
                    val federationDatabase: Option[JobResultsDAL],
                    val ldsmDatabase: LdsmDAL)(implicit system: ActorSystem) extends MiningServiceDoc with PerRequestCreator with DefaultJsonFormats {

  override def context = system
  val routes: Route = mining ~ experiment ~ listMethods

  import ApiJsonSupport._
  import CoordinatorActor._
  import JobDto._

  implicit object EitherErrorSelector extends ErrorSelector[ErrorResponse.type] {
    def apply(v: ErrorResponse.type): StatusCode = StatusCodes.BadRequest
  }

  override def listMethods: Route = path("mining" / "list-methods") {

    import spray.json._

    get {
      respondWithMediaType(`application/json`) {
        complete(MiningService.methods_mock.parseJson.compactPrint)
      }
    }
  }

  override def mining: Route = path("mining" / "job") {
    import FunctionsInOut._

    post {
      entity(as[MiningQuery]) {
        case MiningQuery(variables, covariables, groups, _, Algorithm(c, n, p)) if c == "" || c == "data" => {
          ctx => ctx.complete(ldsmDatabase.queryData({ variables ++ covariables ++ groups }.distinct.map(_.code)))
        }
        case query: MiningQuery => {
          val job = query2job(query)
          chronosJob(RequestProtocol) {
            Start(job)
          }
        }
      }
    }
  }

  override def experiment: Route = path("mining" / "experiment") {
    import FunctionsInOut._

    post {
      entity(as[ExperimentQuery]) {
        case query: ExperimentQuery => {
          val job = query2job(query)
          experimentJob(RequestProtocol) {
            ExperimentActor.Start(job)
          }
        }
      }
    }
  }

  def newCoordinatorActor(jobResultsFactory: JobResults.Factory = JobResults.defaultFactory) = context.actorOf(CoordinatorActor.props(chronosService, resultDatabase, federationDatabase, jobResultsFactory))

  def newExperimentActor(jobResultsFactory: JobResults.Factory = JobResults.defaultFactory) = context.actorOf(Props(classOf[ExperimentActor], chronosService, resultDatabase, federationDatabase, jobResultsFactory))

  def chronosJob(jobResultsFactory: JobResults.Factory = JobResults.defaultFactory)(message : RestMessage): Route =
    ctx => perRequest(ctx, newCoordinatorActor(jobResultsFactory), message)

  def experimentJob(jobResultsFactory: JobResults.Factory = JobResults.defaultFactory)(message : RestMessage): Route =
    ctx => perRequest(ctx, newExperimentActor(jobResultsFactory), message)

}
