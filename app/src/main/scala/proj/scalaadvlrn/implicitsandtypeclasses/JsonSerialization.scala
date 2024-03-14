package proj.scalaadvlrn.implicitsandtypeclasses

import java.util.Date
import org.apache.commons.lang3.StringUtils

object JsonSerialization extends App {

  // data points
  case class User(userId: Int, name: String, dob: Date, email: String)
  case class Post(content: String, postedByUserId: Int, datePosted: Date)
  case class Feed(user: User, posts: List[Post])

  // serializable data points to represent each data point above
  sealed trait JSONElements {
    def stringify: String
  }

  sealed trait JSONValue extends JSONElements

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = s"""$value"""
  }

  final case class JSONInt(value: Int) extends JSONValue {
    override def stringify: String = s"""${Integer.toString(value)}"""
  }

  final case class JSONDate(value: Date) extends JSONValue {
    override def stringify: String = s"""${value.toString}"""
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    override def stringify: String = {
      val tmp = value map (e => s"\"${e.stringify}\",") reduce (_ + _)
      "[" + StringUtils.chop(tmp) + "]"
    }
  }

  final case class JSONObject(mapOfElems: Map[String, JSONValue]) extends JSONElements {
    override def stringify: String = {
      val tmp: String = mapOfElems map {
        case (k: String, v: JSONValue) => s""""$k": "${v.stringify}","""
      } reduce ((a: String, b: String) => a + b)
      "{" + StringUtils.chop(tmp) + "}"
    }
  }

  val testObj: JSONObject = JSONObject(
    Map(
      "user" -> JSONString("abcd"),
      "dob" -> JSONDate(new Date()),
      "userid" -> JSONInt(12312),
      "friendsIds" -> JSONArray(List(JSONInt(12312), JSONInt(231231)))
    )
  )

  println(testObj.stringify)





  // type class to represent conversion of higher value data points
  trait JSONConverter[T] {
    def serialize(input: T): JSONObject
  }

  // type class instances
  implicit object JSONUserConverter extends JSONConverter[User] {
    override def serialize(input: User): JSONObject =
      JSONObject(
        Map(
          "userId" -> JSONInt(input.userId),
          "name" -> JSONString(input.name),
          "dob" -> JSONDate(input.dob),
          "email" -> JSONString(input.email)
        )
      )
  }

  implicit object JSONPostConverter extends JSONConverter[Post] {
    override def serialize(input: Post): JSONObject =
      JSONObject(
        Map(
          "postedByUserId" -> JSONInt(input.postedByUserId),
          "content" -> JSONString(input.content),
          "datePosted" -> JSONDate(input.datePosted)
        )
      )
  }

  // pimped class - that lifts the toJson method into the User and Post case classes
  implicit class JSONOps[T](val input: T) extends AnyVal {
    def toJson(implicit serializer: JSONConverter[T]): JSONObject = serializer.serialize(input)
  }

  val user1 = User(123123, "Jonathan", new Date(), "johnathan@gmail.com")
  val post1 = Post(
    "This is a post made by Jonathan about the time he scored a goal at school",
    123123,
    new Date()
  )

  println(user1.toJson.stringify)
  println(post1.toJson.stringify)

}
