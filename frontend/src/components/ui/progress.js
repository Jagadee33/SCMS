import React from 'react';
import { cn } from '../utils/cn';

const Progress = React.forwardRef(({ className, value = 0, max = 100, children, ...props }, ref) => {
  return (
    <div
      ref={ref}
      className={cn("relative h-4 w-full overflow-hidden rounded-full bg-gray-200", className)}
      {...props}
    >
      <div
        className="h-full bg-blue-600 transition-all duration-300 ease-in-out"
        style={{ width: `${Math.min(100, Math.max(0, (value / max) * 100))}%` }}
      />
      {children && (
        <div className="absolute inset-0 flex items-center justify-center text-xs font-medium text-white">
          {children}
        </div>
      )}
    </div>
  );
});

Progress.displayName = "Progress";

export { Progress };
