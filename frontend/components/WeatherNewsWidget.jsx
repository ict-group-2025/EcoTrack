import React, { useEffect, useState } from "react";
import { ThumbsUp, ThumbsDown, ExternalLink, Clock } from "lucide-react";

const WeatherNewsWidget = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Backend news-service của bạn
  const BASE_URL = import.meta.env.VITE_NEWS_API_URL || "http://localhost:3000";

  useEffect(() => {
    const fetchNews = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await fetch(`${BASE_URL}/news?limit=6`);
        if (!res.ok) throw new Error("Failed to fetch news");

        const data = await res.json();
        setArticles(Array.isArray(data) ? data : data.data || []);
      } catch (err) {
        console.error(err);
        setError("Không thể tải tin tức thời tiết");
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, [BASE_URL]);

  const timeAgo = (date) => {
    const diff = Math.floor((Date.now() - new Date(date)) / 3600000);
    if (Number.isNaN(diff)) return "";
    if (diff < 1) return "Vừa xong";
    if (diff < 24) return `${diff} giờ trước`;
    return `${Math.floor(diff / 24)} ngày trước`;
  };

  // ✅ NEW: click card -> lấy link bài gốc từ /news/:id -> mở thẳng trang báo
  const openOriginalArticle = async (id) => {
    try {
      const res = await fetch(`${BASE_URL}/news/${id}`);
      if (!res.ok) throw new Error("Failed to load news detail");

      const detail = await res.json();
      const link = detail?.link;

      if (link) {
        window.open(link, "_blank", "noopener,noreferrer");
        return;
      }

      // Fallback: nếu không có link thì mở trang detail trong app
      window.location.href = `/news/${id}`;
    } catch (e) {
      console.error(e);
      // Fallback: nếu lỗi vẫn mở trang detail trong app
      window.location.href = `/news/${id}`;
    }
  };

  if (loading) {
    return (
      <div className="bg-white/90 dark:bg-gray-800/90 rounded-3xl p-6 border dark:border-gray-700">
        <h3 className="text-2xl font-bold text-slate-900 dark:text-white mb-6">
          Tin tức thời tiết
        </h3>
        <div className="flex justify-center py-16">
          <div className="w-10 h-10 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white/90 dark:bg-gray-800/90 rounded-3xl p-6 border dark:border-gray-700">
        <p className="text-red-500">{error}</p>
        <p className="text-xs text-slate-500 mt-2">
          Kiểm tra backend có đang chạy ở {BASE_URL} không.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white/90 dark:bg-gray-800/90 backdrop-blur-md rounded-3xl shadow-xl p-6 border border-slate-200 dark:border-gray-700">
      <h3 className="text-2xl font-bold mb-6 text-slate-900 dark:text-white">
        Tin tức thời tiết
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {articles.map((a) => (
          <button
            key={a.id}
            type="button"
            onClick={() => openOriginalArticle(a.id)}
            className="text-left group rounded-2xl overflow-hidden bg-slate-100 dark:bg-gray-900/60 border dark:border-gray-700 hover:shadow-xl hover:scale-[1.02] transition"
          >
            {/* IMAGE */}
            <div className="relative h-44 overflow-hidden">
              <img
                src={
                  a.image_url ||
                  "https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=800"
                }
                alt={a.title}
                className="w-full h-full object-cover group-hover:scale-110 transition-transform"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent" />

              <span className="absolute top-3 right-3 bg-black/60 text-white text-xs px-2 py-1 rounded-full">
                {a.source || "Nguồn"}
              </span>
            </div>

            {/* CONTENT */}
            <div className="p-4">
              <h4 className="font-semibold text-sm text-slate-900 dark:text-white mb-2 line-clamp-2 group-hover:text-emerald-500 transition">
                {a.title}
              </h4>

              <p className="text-xs text-slate-600 dark:text-gray-400 line-clamp-2 mb-3">
                {a.summary}
              </p>

              <div className="flex items-center justify-between text-xs">
                <div className="flex items-center gap-1 text-slate-500 dark:text-gray-500">
                  <Clock className="w-3 h-3" />
                  {timeAgo(a.published_at)}
                </div>

                <div className="flex gap-2">
                  <ThumbsUp className="w-4 h-4 hover:text-emerald-500 transition" />
                  <ThumbsDown className="w-4 h-4 hover:text-red-500 transition" />
                  <ExternalLink className="w-4 h-4 opacity-50" />
                </div>
              </div>
            </div>
          </button>
        ))}
      </div>

      {/* MORE */}
      <div className="mt-6 text-center">
        <button
          onClick={() => window.open(`${BASE_URL}/news`, "_blank")}
          className="px-6 py-3 rounded-xl font-semibold text-white bg-gradient-to-r from-emerald-500 to-green-600 hover:from-emerald-600 hover:to-green-700 transition"
        >
          Xem thêm tin tức
        </button>
      </div>
    </div>
  );
};

export default WeatherNewsWidget;
