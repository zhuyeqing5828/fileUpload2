var fileId;
var fileUploadTest=new fileUpload("testBucket",{
	onLoaded:function(id,file){
		fileId=id;
		console.log(id +"loaded "+file.name);
	},

	onSending:function(fileId,process){
		document.getElementById("bytesRead").innerHTML=process;
	},
	
	onFinished:function(fileId){
		alert(fileId+"finished");
		console.log(fileId +"finished ");
	},
	
	onCenceled:function(fieId){
		alert(fileId+"cencelled");
		console.log(fileId +"cenceled ");
	}
	
	
});

function uploadAndSubmit(){
	var form = document.forms["demoForm"];
	this.fileUpload;
	if (form["file"].files.length > 0)
	{
		var file3 = form["file"].files[0];
		document.getElementById("bytesTotal").innerHTML=file3.size;
		fileUploadTest.addUploadMission(file3);
	}

}
function cencel(){
	console.log(fileId);
	fileUploadTest.cencelUploadMission(fileId);
}