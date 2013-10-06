function jsDate() {
	var date = new Date();
	dateString = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear().toString().substr(2,2);
	alert("Welcome " + dateString);
	return dateString;
}