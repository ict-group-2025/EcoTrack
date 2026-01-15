import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";

const NewsDetail = () => {
  const { id } = useParams();

  const BASE_URL = useMemo(
    () => import.meta.env.VITE_NEWS_API_URL || "http://localhost:3000",
    []
  );

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        setLoading(true);
        setErr(null);

        const res = await fetch(`${BASE_URL}/news/${id}`);
        if (!res.ok) throw new Error("Failed to load news detail");

        const json = await res.json();
        setData(json);
      } catch (e) {
        console.error(e);
        setErr("Không thể tải chi tiết bài viết.");
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [BASE_URL, id]);

  const formatTime = (t) => {
    if (!t) return "";
    const d = new Date(t);
    if (Number.isNaN(d.getTime())) return "";
    return d.toLocaleString();
  };

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="w-10 h-10 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (err) {
    return (
      <div className="p-6 max-w-4xl mx-auto">
        <div className="bg-white/90 dark:bg-gray-800/90 rounded-3xl p-6 border dark:border-gray-700">
          <p className="text-red-500 font-semibold">{err}</p>
          <p className="text-xs text-slate-500 mt-2">
            Kiểm tra backend có đang chạy ở {BASE_URL} không.
          </p>
          <div className="mt-4">
            <Link
              to="/"
              className="inline-block px-4 py-2 rounded-xl bg-emerald-600 text-white hover:bg-emerald-700 transition"
            >
              Quay về trang chủ
            </Link>
          </div>
        </div>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="p-6 max-w-4xl mx-auto">
        <div className="bg-white/90 dark:bg-gray-800/90 rounded-3xl p-6 border dark:border-gray-700">
          <p className="text-slate-700 dark:text-slate-200">
            Không có dữ liệu bài viết.
          </p>
          <div className="mt-4">
            <Link
              to="/"
              className="inline-block px-4 py-2 rounded-xl bg-emerald-600 text-white hover:bg-emerald-700 transition"
            >
              Quay về trang chủ
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-4">
        <Link
          to="/"
          className="text-emerald-600 hover:text-emerald-700 font-semibold"
        >
          ← Quay về
        </Link>
      </div>

      <div className="bg-white/90 dark:bg-gray-800/90 rounded-3xl p-6 border border-slate-200 dark:border-gray-700 shadow-xl">
        <h1 className="text-2xl md:text-3xl font-bold text-slate-900 dark:text-white mb-3">
          {data.title}
        </h1>

        <div className="text-sm text-slate-600 dark:text-gray-400 mb-5">
          <span className="font-medium">{data.source || "Nguồn"}</span>
          {data.published_at ? (
            <span> • {formatTime(data.published_at)}</span>
          ) : null}
          {data.category ? <span> • {data.category}</span> : null}
          {data.location ? <span> • {data.location}</span> : null}
        </div>

        {data.image_url ? (
          <img
            src={data.image_url}
            alt={data.title}
            className="w-full rounded-2xl mb-6 object-cover"
          />
        ) : null}

        {/* Content: ưu tiên content, nếu không có thì summary */}
        {data.content ? (
          <div
            className="prose dark:prose-invert max-w-none"
            dangerouslySetInnerHTML={{ __html: data.content }}
          />
        ) : (
          <p className="text-slate-700 dark:text-slate-200 leading-relaxed">
            {data.summary || "Không có nội dung chi tiết."}
          </p>
        )}

        {/* Link bài gốc (backend trả field 'link') */}
        {data.link ? (
          <div className="mt-6">
            <a
              href={data.link}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block px-4 py-2 rounded-xl bg-emerald-600 text-white hover:bg-emerald-700 transition"
            >
              Mở bài gốc
            </a>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default NewsDetail;
