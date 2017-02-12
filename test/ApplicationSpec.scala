import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerSuite {

  "Routes" should {
    "redirect to 404 page on an invalid URL path" in  {
      val nonExistentPath = route(app, FakeRequest(GET, "/fake-request")).get
      val h = headers(nonExistentPath)
      h("Location") mustBe "/not-found"
      status(nonExistentPath) mustBe SEE_OTHER
    }
  }

  "Application" should {
    // Application.showGridView tests
    "render the grid view" in {
      val gridView = route(app, FakeRequest(GET, "/grid")).get
      status(gridView) mustBe OK
      contentAsString(gridView) must include ("Grid view")
    }

    // Application.showListView tests
    "render the list view" in {
      val listView = route(app, FakeRequest(GET, "/list")).get
      status(listView) mustBe OK
      contentAsString(listView) must include ("List view")
    }

    // Application.showPlace() tests
    "show place" in {
      val place = route(app, FakeRequest(GET, "/show/3")).get
      status(place) mustBe OK
      contentAsString(place) must include ("London")
    }
    "fail on non-existent place" in {
      val nonExistentPlace = route(app, FakeRequest(GET, "/show/9999")).get
      status(nonExistentPlace) mustBe SEE_OTHER
    }

    // Application.showPlaceForm() tests
    "render the Place form" in {
      val placeForm = route(app, FakeRequest(GET, "/add")).get
      status(placeForm) mustBe OK
    }

    // Application.getPictureOfPlace() tests
    "get picture of place" in {
      val pictureOfPlace = route(app, FakeRequest(GET, "/picture/3")).get
      status(pictureOfPlace) mustBe OK
    }
    "fail on non-existent picture" in {
      val pictureOfNonExistentPlace = route(app, FakeRequest(GET, "/picture/9999")).get
      status(pictureOfNonExistentPlace) mustBe NOT_FOUND
    }


//    // TODO Need to implement a Writable to include multipart form data in a request
//    "add a mock place" in {
//      val placeData: Map[String, Seq[String]] = Map(
//        "id" -> Seq("0"),
//        "name" -> Seq("mockName"),
//        "country" -> Seq("mockCountry"),
//        "description" -> Seq("mockDescription")
//      )
//
//      val tempFile = TemporaryFile(new File("public/images/favicon.png"))
//      val part = FilePart[TemporaryFile]("picture", tempFile.file.getAbsolutePath, Some("image/png"), tempFile)
//      val formData: MultipartFormData[TemporaryFile] = MultipartFormData(dataParts = placeData, files = Seq(part), badParts = Seq())
//      val addResultOpt = route(app, FakeRequest(POST, "/add").withMultipartFormDataBody(formData)).get
//      //val addResultOpt = route(app, FakeRequest(POST, "/add", headers = FakeHeaders(), body = AnyContentAsMultipartFormData(formData)))
//
////      println(addResult)
////      status(addResult) mustBe SEE_OTHER
//      OK mustBe NOT_FOUND
//    }


    // Application.editPlace() tests
    "edit place" in {
      val editPlace = route(app, FakeRequest(GET, "/edit/3")).get
      status(editPlace) mustBe OK
    }

    // Application.deletePlace() tests
    "delete place" in {
      val id = 3
      val deletePlace = route(app, FakeRequest(DELETE, s"/delete/$id")).get
      status(deletePlace) mustBe SEE_OTHER
      val h = headers(deletePlace)
      h("Location") mustBe "/grid"
      h("Set-Cookie") must include (s"Deleted+place+with+ID+$id")
    }
    "fail on deleting non-existent place" in {
      val id = 9999
      val deletePlace = route(app, FakeRequest(DELETE, s"/delete/$id")).get
      ScalaFutures.whenReady(deletePlace.failed) { e =>
        e mustBe a [NoSuchElementException]
      }
    }
  }


}
