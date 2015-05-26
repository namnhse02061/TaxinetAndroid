package vn.co.taxinet.mobile.googleapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJSONParser {
	/** Receives a JSONObject and returns a list */
	public List<HashMap<String, String>> parse(JSONObject jObject) {

		Double lat = Double.valueOf(0);
		Double lng = Double.valueOf(0);
		String formattedAddress = "", country = "", city = "", address = "";

		HashMap<String, String> hm = new HashMap<String, String>();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			lat = (Double) jObject.getJSONObject("result")
					.getJSONObject("geometry").getJSONObject("location")
					.get("lat");
			lng = (Double) jObject.getJSONObject("result")
					.getJSONObject("geometry").getJSONObject("location")
					.get("lng");
			formattedAddress = (String) jObject.getJSONObject("result").get(
					"formatted_address");
			JSONArray jsonArray = (JSONArray) jObject.getJSONObject("result")
					.get("address_components");
			for (int i = 0; i < jsonArray.length(); i++) {
				String result = jsonArray.getJSONObject(i).getString(
						"long_name");
				if (jsonArray.getJSONObject(i).getJSONArray("types").get(0)
						.toString().equalsIgnoreCase("country")) {
					country = result;
				} else if (jsonArray.getJSONObject(i).getJSONArray("types")
						.get(0).toString()
						.equalsIgnoreCase("administrative_area_level_1")) {
					city = result;
				} else {
					address += result + " ";
				}
			}
			System.out.println("lat " + lat);
			System.out.println("lat :" + lng);
			System.out.println("tuan oc cho " + jObject.toString());
			hm.put("lat", Double.toString(lat));
			hm.put("lng", Double.toString(lng));
			hm.put("formatted_address", formattedAddress);
			hm.put("country", country);
			hm.put("city", city);
			hm.put("address", address);

			list.add(hm);

			return list;
		} catch (JSONException e) {
		} catch (Exception e) {
		}
		return null;
	}
}
