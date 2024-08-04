import React from "react";
import { Link } from "react-router-dom";

function ErrorPage() {
  return (
    <div className="flex flex-col text-center text-2xl font-bold text-red-600">
      Oops! The page you're looking for doesn't exist.

      <Link to="/" className="flex mx-auto w-fit mt-4 px-4 py-2 bg-blue-500 text-white rounded-md hover:text-white">Home</Link>
    </div>
  );
}

export default ErrorPage;
