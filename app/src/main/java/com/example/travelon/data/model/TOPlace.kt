import com.example.travelon.data.model.Geometry
import com.example.travelon.data.model.Photos
import com.google.firebase.database.Exclude

class TOPlace (

	val formatted_address : String = "",
	val geometry : Geometry? = null,
	val icon : String? = null,
	val id : String = "",
	val name : String = "",
	val photos : List<Photos>? = null,
	val place_id : String = "",
	val reference : String? = null,
	val types : List<String>? = null,
	var favourite: Boolean = false
) {
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"place_id" to place_id,
			"formatted_address" to formatted_address,
			"name" to name,
			"reference" to reference,
			"is_favourite" to favourite
		)
	}
}