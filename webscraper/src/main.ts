/*import { CheerioCrawler } from "crawlee";

const crawler = new CheerioCrawler({
  maxRequestsPerCrawl: 20,
  requestHandler: async ({ $, request, enqueueLinks }) => {
    const title = $("title").text();
    console.log(`The title of "${request.url}" is: ${title}.`);
    await enqueueLinks();
  },
});

await crawler.run(["https://crawlee.dev"]);*/