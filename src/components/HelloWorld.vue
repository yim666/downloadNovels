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
              let exclamationCount = 0;

              for (let i = 0; i < text.length; i++) {
                  const char = text[i];
                  if (char === '。') {
                      dotCount++;
                  }

                  formatted += char;
                  if (dotCount + exclamationCount === 3) {
                      if(text[i+1] ==='”' ||(text[i+1] == '<' &&text[i+2] =='b')) continue;
                      formatted += '<br/>';
                      dotCount = 0;
                      exclamationCount = 0;
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
                text = text.replace(/漫威里的机械狂潮\S*\s+\S*\s+/g, '<br><br><h2>$&</h2>')

                // // 将双引号包裹的内容转换为带换行的段落
                text = text.replace(/“(.*?)”/g, '<p>"$1"</p>')
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
