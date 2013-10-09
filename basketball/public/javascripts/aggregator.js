function todayDate() {
	var date = new Date();
	var dateString = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
	return dateString;
}