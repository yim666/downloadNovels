<template>
    <div>
        <div style="background-color: rgba(255,248,230,0.93)">
            <div class="content" v-html="fileContent"></div>

        </div>
    </div>
</template>

<script>
    // import axios from "core-js/internals/queue";

    export default {
        name: 'HelloWorld',
        data() {
            return {
                fileContent: '',
                filePath: 'txtcontent/novel2.txt'
            };
        },
        methods:{
          repa(text){
              let formatted = '';
              let dotCount = 0;

              for (let i = 0; i < text.length; i++) {
                  const char = text[i];
                  if (char === '。' || char ==='!' || char==='?') {
                      dotCount++;
                  }

                  formatted += char;
                  if (dotCount === 3) {
                      if (text[i + 1] === '”' || (text[i + 1] == '<' && text[i + 2] == 'b')) continue;
                      formatted += '<br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
                      dotCount++;
                  }else if (dotCount  === 7) {
                      if(text[i+1] ==='”' ||(text[i+1] == '<' &&text[i+2] =='b')) continue;
                      formatted += '<br/><br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
                      dotCount = 0;
                  }

              }
              return formatted
          }
        },
        async created() {
            try {
                const response = await fetch(this.filePath)
                let text = await response.text()
                text =this.repa(text)
                //以漫开头 紧跟着 多个字符(\S*) 然后一个空格(\s+) 再多个字符 再一个空格
                // text = text.replace(/title:\S*\s+\S*\s+/g, '<br><br><h2>$&</h2>')

                // // 将双引号包裹的内容转换为带换行的段落
                text = text.replace(/【稳定运行多年的小说app，媲美老版追书神器，老书虫都在用的换源App，huanyuanapp.com】/g, '')
                text = text.replace(/【新章节更新迟缓的问题，在能换源的app上终于有了解决之道，这里下载 huanyuanapp.com 换源App, 同时查看本书在多个站点的最新章节。】/g, '')
                text = text.replace(/【鉴于大环境如此，本站可能随时关闭，请大家尽快移步至永久运营的换源App，huanyuanapp.com 】/g, '')
                text = text.replace(/点击下载本站APP,海量小说，免费畅读！/g, '')
                text = text.replace(/【目前用下来，听书声音最全最好用的App，集成4大语音合成引擎，超100种音色，更是支持离线朗读的换源神器，huanyuanapp.com 换源App】/g, '')
                // // 将双空格转换为换行符
                // text = text.replace(/ {2}/g, '<br/>')


                this.fileContent = text
            } catch (error) {
                console.error(error)
            }
        }
    }
</script>

<style scoped>
    .content {
        width: 60%;
        margin: 0 auto;
        text-align: left;
        font-size: 22px;
        color: #333;
        line-height: 2.2;
        letter-spacing: 1.5px;
    }
</style>
