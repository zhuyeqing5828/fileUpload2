/**
 * dependOn spart-md5.min.js
 * caculate a file'sMD5Code
 * @param file
 * @param onFinished on Code caculated ,call the method
 */
function MD5Check(file,onFinished) {
	var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
		chunkSize = 2097152, // read in chunks of 2MB
		chunks = Math.ceil(file.size / chunkSize),
		currentChunk = 0,
		spark = new SparkMD5.ArrayBuffer(),
		frOnload = function(e){
			console.log("\nread chunk number "+parseInt(currentChunk+1)+" of "+chunks);
			spark.append(e.target.result); // append array buffer
			currentChunk++;
			if (currentChunk < chunks)
				loadNext();
			else
					onFinished(spark.end());
		},
		frOnerror = function () {
			onFinished();
		};
	function loadNext() {
		var fileReader = new FileReader();
		fileReader.onload = frOnload;
		fileReader.onerror = frOnerror;
		var start = currentChunk * chunkSize,
			end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
		fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
	};
	console.log("file name: "+file.name+" ("+file.size.toString().replace(/\B(?=(?:\d{3})+(?!\d))/g, ',')+" bytes)\n");
	loadNext();
}