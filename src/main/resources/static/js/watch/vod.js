document.addEventListener("DOMContentLoaded", async function () {
    const title = document.getElementById('vod').getAttribute('value');
    await streamVideo(title);
});

async function streamVideo(title) {
    const videoElement = document.getElementById('vod');

    try {
        // GET 요청으로 VOD 데이터를 가져옴
        const response = await axios.get(`/stream/vod/fetch/${title}`, {
            responseType: 'blob', // 바이너리 데이터를 받기 위해 blob 설정
            headers: {
                Range: 'bytes=0-' // 원하는 바이트 범위 지정 (예: 처음부터 끝까지)
            }
        });

        console.log(response.data);

        // Blob URL 생성
        const videoBlob = new Blob([response.data], { type: response.headers['content-type'] });

        // 비디오 태그에 URL 설정
        videoElement.src = URL.createObjectURL(videoBlob);
    } catch (error) {
        console.error('Error streaming video:', error);
    }
}