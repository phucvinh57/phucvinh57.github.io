import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { RouterProvider } from "react-router-dom";
import { router } from "./router";

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<div className="p-5 max-w-screen-lg relative left-1/2 -translate-x-1/2">
			<RouterProvider router={router} />
		</div>
	</StrictMode>,
);
